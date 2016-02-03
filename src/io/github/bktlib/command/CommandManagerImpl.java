/*
 *  Copyright (C) 2016 Leonardosc
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package io.github.bktlib.command;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.github.bktlib.reflect.util.ReflectUtil.hasPublicConstructor;
import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

import io.github.bktlib.command.annotation.Command;
import io.github.bktlib.command.annotation.SubCommand;
import io.github.bktlib.command.args.CommandArgs;
import io.github.bktlib.common.Strings;
import io.github.bktlib.reflect.FieldAccessor;
import io.github.bktlib.reflect.MethodAccessor;
import io.github.bktlib.reflect.MethodRef;
import io.github.bktlib.reflect.util.MCReflectUtil;
import io.github.bktlib.reflect.util.ReflectUtil;

/**
 * Implementacao default do {@link CommandManager}
 */
@SuppressWarnings("unchecked")
class CommandManagerImpl implements CommandManager
{	
	private File cachedRegisterAllFile;
	private LoadingCache<String, Optional<CommandBase>> byNameCache;
	private LoadingCache<Class<?>, Optional<CommandBase>> byClassCache;
    private LoadingCache<Class<?>, Object> classToInstanceCache;

	private final SimpleCommandMap commandMap;
	private final Logger logger;
	private final Plugin owner;

	CommandManagerImpl(final Plugin plugin)
	{
		logger 		= plugin.getLogger();
		owner 		= plugin;
		commandMap 	= getCommandMap();

		initCaches();
	}

	@Override
	public void register( CommandBase command )
	{
		checkNotNull( command, "command cannot be null" );

		commandMap.register(
				owner.getName(),
				new CommandAdapter( command ) );

		command.subCommands = parseSubCommands( command );
	}

	@Override
	public void registerMethod( Class<?> methodClass, String methodName )
	{
		checkNotNull( methodClass, "methodClass cannot be null " );

		checkArgument( hasPublicConstructor( methodClass ),
				"methodClass must have at least one public constructor." );

		checkArgument( (methodClass.getModifiers() & Modifier.ABSTRACT) == 0,
				"methodClass cannot be abstract." );

        try
        {
            registerMethod( classToInstanceCache.get( methodClass ), methodName );
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
        }
    }

	@Override
	public void registerMethod( Object instance, String methodName )
	{
		checkNotNull( instance, "instance cannot be null" );
		checkArgument( !Strings.isNullOrEmpty( methodName ), "methodName cannot be null or empty" );

		final Class<?> instClass = instance.getClass();

		try
		{
			final Method method = instClass.getDeclaredMethod(
                                    methodName,
                                    CommandSource.class,
                                    CommandArgs.class );

            if ( method.getReturnType() != CommandResult.class )
                throw new NoSuchMethodException();

			if ( !method.isAnnotationPresent( Command.class ) )
			{
				logger.log( Level.SEVERE, format( "Method '%s.%s(CommandSource, CommandArgs)' must have '%s' annotation.",
						instClass.getName(), methodName, Command.class.getName() ) );
			}
			else
			{
				register( new MethodCommand( MethodRef.of( instance, method ) ) );
			}
		}
		catch ( NoSuchMethodException e )
		{
			logger.log( Level.SEVERE, format( "Could not find method 'CommandResult %s.%s(CommandSource, CommandArgs)'.",
					instClass.getName(), methodName ) );
			logger.log( Level.SEVERE, "Be sure that's signature is correct, signature must be like that "
					+ "'CommandResult methodName(CommandSource, CommandArgs)'." );
		}
	}
	
	@Override
	public void register( Class<? extends CommandBase> commandClass )
	{
		checkNotNull( commandClass, "commandClass cannot be null " );

		if ( !canInstantiate( commandClass ) )
			return;

		{ // new scope

			final Class<?> enclosingClass = commandClass.getEnclosingClass();

			if ( enclosingClass != null && canInstantiate( enclosingClass ) &&
					!Modifier.isStatic( commandClass.getModifiers() ) )
			{
				try
				{
					Object enclosingInst = classToInstanceCache.get(enclosingClass);

					Object nestedClassInst = commandClass.getConstructor( enclosingClass )
							.newInstance( enclosingInst );

					register( (CommandBase) nestedClassInst );

					return;
				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}
			}

		} // end of new scope

		try
		{
			register( (CommandBase) classToInstanceCache.get(commandClass) );
		}
		catch ( ExecutionException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void registerAll()
	{
		try
		{
			if ( cachedRegisterAllFile == null )
			{
				MethodAccessor<File> getFileAccessor = MethodAccessor.access( owner, "getFile" );
				Optional<File> jarFile = getFileAccessor.invoke();

				if ( !jarFile.isPresent() )
					logger.log( Level.SEVERE, "Something went wrong, plugin file is null." );
				else
					cachedRegisterAllFile = jarFile.get();
			}

			final JarInputStream is = new JarInputStream( new FileInputStream( cachedRegisterAllFile ) );

			for ( JarEntry entry; (entry = is.getNextJarEntry()) != null; )
			{
				final String entryName = entry.getName();

				if ( !entryName.endsWith( ".class" ) )
					continue;

				Class<?> klass = Class.forName(
						entry.getName().replace( '/', '.' ).substring( 0, entry.getName().length() - 6 ) );

				if ( klass != MethodCommand.class && klass != CommandBase.class &&
						CommandBase.class.isAssignableFrom( klass ) )
				{
					register( (Class<? extends CommandBase>) klass );
				}

				if ( ReflectUtil.isConcreteClass( klass ) )
				{
					registerAll( klass );
				}
			}

			is.close();

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void registerAll( Class<?> klass )
	{
		checkNotNull( klass, "klass cannot be null" );
		
		Stream.of( klass.getDeclaredMethods() )
    		.filter( method -> method.isAnnotationPresent( Command.class ) )
    		.filter( method -> !method.isAnnotationPresent( SubCommand.class ) )
    		.forEach( method -> registerMethod( klass, method.getName() ) );
	}

	@Override
	public <T extends CommandBase> Optional<T> getCommandByClass( Class<T> klass )
	{
		checkNotNull( klass, "klass cannot be null" );

		try
		{
			return (Optional<T>) byClassCache.get( klass );
		}
		catch ( ExecutionException e )
		{
			e.printStackTrace();
		}

		return Optional.empty();
	}

	@Override
	public Optional<CommandBase> getCommandByName( final String name )
	{
		checkArgument( !Strings.isNullOrEmpty( name ), "name cannot be null or empty." );
		try
		{
			return byNameCache.get( name );
		}
		catch ( ExecutionException e )
		{
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Plugin getOwner()
	{
		return owner;
	}
	
	/**
	 * Checa se a classe tem construtores visiveis, caso nao tenha ele informa e
	 * retorna falso
	 */
	private boolean canInstantiate( Class<?> klass )
	{
		if ( !hasPublicConstructor( klass ) )
		{
			logger.log( Level.SEVERE, format( "Could not register %s command " +
					"because it has no public constructors.", klass ) );

			return false;
		}

		return true;
	}
	
	private SimpleCommandMap getCommandMap()
	{
		final PluginManager pluginManager = getOwner().getServer().getPluginManager();

		FieldAccessor<SimpleCommandMap> cmdMapField = FieldAccessor.access(
				pluginManager, "commandMap" );

		Optional<SimpleCommandMap> optCmdMap = cmdMapField.getValue();

		if ( optCmdMap.isPresent() )
			return optCmdMap.get();

		final String message = String.format( "Cound not get commandMap, CraftBukkit Version: %s, PluginManager: %s",
				MCReflectUtil.getCBVersion(),
				pluginManager );

		throw new IllegalStateException( message );
	}
	
	private void initCaches()
	{
		final CacheLoader<String, Optional<CommandBase>> byNameLoader = new CacheLoader<String, Optional<CommandBase>>()
		{
			@Override
			public Optional<CommandBase> load( @Nonnull String key )
			{
				org.bukkit.command.Command bukkitCommand = commandMap.getCommand( key );

				if ( !(bukkitCommand instanceof CommandAdapter) )
					return Optional.empty();

				return Optional.of( ((CommandAdapter) bukkitCommand).getCommandBase() );
			}
		};

		final CacheLoader<Class<?>, Optional<CommandBase>> byClassLoader = new CacheLoader<Class<?>, Optional<CommandBase>>()
		{
			@Override
			public Optional<CommandBase> load( @Nonnull Class<?> key )
			{
				Optional<CommandBase> optCommand = commandMap.getCommands()
						.parallelStream()
						.filter( cmd -> cmd.getClass().equals( key ) )
						.filter( cmd -> cmd instanceof CommandAdapter )
						.map( cmd -> ((CommandAdapter) cmd).getCommandBase() )
						.findAny();

				return Optional.ofNullable( optCommand.orElse( null ) );
			}
		};

        final CacheLoader<Class<?>, Object> classToInstanceLoader = new CacheLoader<Class<?>, Object>()
        {
            @Override
            public Object load( @Nonnull Class<?> aClass ) throws Exception
            {
                return aClass.newInstance();
            }
        };

		final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
				.weakValues()
				.weakKeys()
				.maximumSize( 1000 )
				.expireAfterAccess( 5, TimeUnit.MINUTES );

		byNameCache = cacheBuilder.build( byNameLoader );
		byClassCache = cacheBuilder.build( byClassLoader );
        classToInstanceCache = cacheBuilder.build( classToInstanceLoader );
	}

	/**
	 * Converte a {@link Command#subCommands() lista de sub comandos} em um
	 * {@code Map<String, CommandBase>}, tendo como chave o
	 * {@link Command#name() nome} do sub comando, e como valor, a instancia do
	 * sub comando em sí.
	 */
	Map<String, CommandBase> parseSubCommands( CommandBase command )
	{
		final Map<String, CommandBase> subCommands = Maps.newHashMap();

		Stream.of( command.commandAnnotation.subCommands() )
			.peek( raw -> {
				if ( !raw.contains( "::" ) || Strings.isNullOrEmpty( raw.split( "::" )[0] ) )
				{
					invalidSubCmd( "Expected 'package.ClassName::methodName'",
							raw, command );
				}
			} )
			.map( raw -> raw.split( "::" ) )
			.forEach( parsed -> {
				final String rawSubCommand = Strings.of( parsed[0], "::", parsed[1] );
				final String methodName = parsed[1];
				String className = parsed[0];

				Class<?> klass = null;

				if ( className.equalsIgnoreCase( "this" ) )
				{
					if ( command instanceof MethodCommand )
						klass = ((MethodCommand) command).ref.getOwner().getClass();
					else
						klass = command.getClass();
				}
				else
				{
					if ( !className.contains( "." ) )
					{
						className = Strings.of( command.getClass().getPackage().getName(),
								".", className );
					}

					try
					{
						klass = Class.forName( className );
					}
					catch ( ClassNotFoundException e )
					{
						invalidSubCmd( "Class %s not found.", rawSubCommand, 
								command, e.getMessage() );
					}
				}

				if ( !ReflectUtil.isConcreteClass( klass ) )
				{
					invalidSubCmd( "Class '%s' isn't a concrete class.", rawSubCommand, 
							command, klass.getName() );
				}

				Object klassInstance = null;

				try
				{
					klassInstance = classToInstanceCache.get(klass);
				}
				catch ( Exception e )
				{
					/**
					 * Isso é pra nunca acontecer pois as verificaçoes ja
					 * foram feitas a cima, o que pode acontecer é um erro
					 * de segurança por exemplo.
                     *
                     * Agora pode acontecer, por causa do cache.
					 */
					e.printStackTrace();
				}

				/**
				 * Lambda requer que as variveis sejam estaticas.
				 */
				final Object finalKlassInstance = klassInstance;
				final Class<?> finalKlass = klass;

				final Consumer<String> registerSubCmd = mdName ->
				{
					Method subCmdMethod = null;

					try
					{
						// TODO: parametros serao alterados para (CommandSource,CommandArgs,CommandBase)
						subCmdMethod = finalKlass.getDeclaredMethod( mdName,
								CommandSource.class, CommandArgs.class );

					}
					catch ( NoSuchMethodException e )
					{
						invalidSubCmd( "Method '%s' not found.", rawSubCommand, 
								command, mdName );
					}

					final SubCommand subCmdAnnotation = subCmdMethod.getAnnotation( SubCommand.class );

					if ( subCmdAnnotation == null )
					{
						invalidSubCmd( "The '%s' method does not contains 'SubCommand' annotation.",
								rawSubCommand, command, methodName );
					}

					/**
					 * "Converte" a anotação SubCommand para Command para
					 * ser usada no construtor do MethodCommand, as duas
					 * anotações são exatamente iguais, porem, como o java
					 * não permite herança com anotações eu tive que fazer
					 * isso.
					 */
					final Command commandAnnotation = new Command()
					{
						@Override
						public Class<? extends Annotation> annotationType()
						{
							return Command.class;
						}

						@Override
						public String name()
						{
							return subCmdAnnotation.name();
						}

						@Override
						public String permission()
						{
							return subCmdAnnotation.permission();
						}

						@Override
						public String description()
						{
							return subCmdAnnotation.description();
						}

						@Override
						public String usage()
						{
							return subCmdAnnotation.usage();
						}

						@Override
						public String[] aliases()
						{
							return subCmdAnnotation.aliases();
						}

						@Override
						public String[] subCommands()
						{
							return subCmdAnnotation.subCommands();
						}

						@Override
						public UsageTarget usageTarget()
						{
							return subCmdAnnotation.usageTarget();
						}
					};

					/**
					 * Por equanto os subcomandos poderão ser apenas
					 * métodos.
					 * 
					 * Talvez eu adicione para poder colocar classes como
					 * sub comandos, não sei...
					 */
					final MethodCommand subCommand = new MethodCommand(
							MethodRef.of( finalKlassInstance, subCmdMethod ),
							commandAnnotation );

					subCommands.put( subCommand.getName(), subCommand );

					if ( subCmdAnnotation.subCommands().length != 0 )
					{
						subCommand.subCommands = parseSubCommands( subCommand );
					}
				};

				if ( methodName.equals( "*" ) ) // all commands
				{
					Stream.of( klass.getDeclaredMethods() )
							.filter( md -> md.isAnnotationPresent( SubCommand.class ) )
							.map( Method::getName )
							.forEach( registerSubCmd );
				}
				else if ( methodName.startsWith( "[" ) && methodName.endsWith( "]" ) )
				{
					Splitter.on( ',' )
							.trimResults()
							.omitEmptyStrings()
							.split( CharMatcher.anyOf( "[]" ).removeFrom( methodName ) )
							.forEach( registerSubCmd );
				}
				else
				{
					registerSubCmd.accept( methodName );
				}
			} );

		return subCommands;
	}

    private static void invalidSubCmd( final String reason, final Object ... args )
    {
        throw new IllegalArgumentException( String.format(
                "Invalid subCommand '%s' in '%s' command. " + reason,
                args
        ));
    }
}
