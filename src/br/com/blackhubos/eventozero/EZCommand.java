package br.com.blackhubos.eventozero;

import java.util.Optional;

import com.google.common.base.CharMatcher;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import br.com.blackhubos.eventozero.factory.Event;
import br.com.blackhubos.eventozero.factory.EventFlags.Flag;
import br.com.blackhubos.eventozero.factory.EventState;
import br.com.blackhubos.eventozero.handlers.MessageHandler;
import br.com.blackhubos.eventozero.util.Framework;
import io.github.bktlib.command.CommandBase;
import io.github.bktlib.command.CommandResult;
import io.github.bktlib.command.CommandSource;
import io.github.bktlib.command.annotation.Command;
import io.github.bktlib.command.annotation.SubCommand;
import io.github.bktlib.command.args.CommandArgs;

@Command ( name = "eventozero", aliases = "ez", description = "Comando de Gerenciamento do EventoZero", subCommands = "this::*" )
public final class EZCommand extends CommandBase
{

	/**
	 * TODO: fazer sistema de pagina��o.
	 */
	@Override
	public CommandResult onExecute ( final CommandSource src, final CommandArgs args )
	{
		final boolean isConsole = src.isConsole();
		this.getSubCommands().stream().map( subCmd ->
		{
			String st = String.format( "�6� �9/ez %s %s �f= �6%s", subCmd.getName(), subCmd.getUsage().orElse( " " ), subCmd.getDescription().orElse( "Sem descri��o" ) );
			if ( isConsole )
			{
				st = CharMatcher.ASCII.negate().removeFrom( st );
			}

			return st;
		} ).forEach( src::sendMessage );

		return CommandResult.success();
	}

	@SubCommand ( name = "iniciar", aliases = { "start", "init", "come�ar", "play" }, usage = "<evento>", description = "Iniciar um evento manualmente" )
	private CommandResult iniciar ( final CommandSource src, final CommandArgs args )
	{
		if ( args.size() == 1 )
		{
			final String name = Framework.normalize( args.get( 0 ).toLowerCase() );
			final Optional< Event > event = EventoZero.getEventHandler().getEventByName( name );
			if ( event.isPresent() )
			{
				if ( event.get().getState() == EventState.CLOSED ) // TODO: verificar se esse 'CLOSED' esta dentro da minha logica
				{
					event.get().start();
					return CommandResult.success();
				}
				else
				{
					return CommandResult.success();
				}
			}
			else
			{
				MessageHandler.EVENTO_NAO_ENCONTRADO.send( src.toCommandSender() );
				return CommandResult.success();
			}
		}
		else
		{
			final String s = String.format( "�9Utilize o comando �f/ez %s %s �9para �6%s�9.", "iniciar", "<evento>", "iniciar um evento manualmente" );
			src.sendMessage( s );
			return CommandResult.success();
		}
	}

	@SubCommand ( name = "cancelar", aliases = { "cancel", "stop", "forcestop", "forcecancel" }, usage = "<evento>", description = "Cancela um evento ocorrendo" )
	private CommandResult cancelar ( final CommandSource src, final CommandArgs args )
	{
		if ( args.size() == 1 )
		{
			final String name = Framework.normalize( args.get( 0 ).toLowerCase() );
			final Optional< Event > event = EventoZero.getEventHandler().getEventByName( name );
			if ( event.isPresent() )
			{
				if ( event.get().getState() != EventState.CLOSED ) // TODO: verificar se esse 'CLOSED' esta dentro da minha logica #Crazy: CLOSED seria que já está terminado, foi finalizado ou NÃO ESTÁ ABERTO. OPEN está aberto
				{
					event.get().stop();
					return CommandResult.success();
				}
				else
				{
					return CommandResult.success();
				}
			}
			else
			{
				MessageHandler.EVENTO_NAO_ENCONTRADO.send( src.toCommandSender() );
				return CommandResult.success();
			}
		}
		else
		{
			final String s = String.format( "�9Utilize o comando �f/ez %s %s �9para �6%s�9.", "cancelar", "<evento>", "cancelar um evento ocorrendo" );
			src.sendMessage( s );
			return CommandResult.success();
		}
	}

	@SubCommand ( name = "addentrada", aliases = { "addentry", "nextentry", "nextentrada", "nexten", "putentry" }, description = "Adiciona uma nova entrada" )
	private CommandResult addentrada ( final CommandSource src, final CommandArgs args )
	{
		if ( src.isConsole() )
		{
			src.sendMessage( "This command hasn't support at console." );
			return CommandResult.success();
		}

		if ( args.size() == 1 )
		{
			final String name = Framework.normalize( args.get( 0 ).toLowerCase() );
			final Optional< Event > event = EventoZero.getEventHandler().getEventByName( name );
			if ( event.isPresent() )
			{
				if ( event.get().getState() == EventState.CLOSED ) // TODO: verificar se esse 'CLOSED' esta dentro da minha logica
				{
					final Location pos = ( ( Player ) src.toCommandSender() ).getLocation();
					event.get().addEntrada( pos );
					// TODO: entrada adicionada com ID!!!!
					src.sendMessage( String.format( "�9%s: �f%s�9, %s: �f%s�9, %s: �f%s�9, %s: �f%s", "X", "World", pos.getWorld().getName(), pos.getBlockX(), "Y", pos.getBlockY(), "Z", pos.getBlockZ() ) );
					return CommandResult.success();
				}
				else
				{
					// TODO: edi��es n�o s�o permitidas com o evento rodando
					return CommandResult.success();
				}
			}
			else
			{
				MessageHandler.EVENTO_NAO_ENCONTRADO.send( src.toCommandSender() );
				return CommandResult.success();
			}
		}
		else
		{
			final String s = String.format( "�9Utilize o comando �f/ez %s %s �9para �6%s�9.", "addentrada", "<evento>", "adicionar uma nova entrada" );
			src.sendMessage( s );
			return CommandResult.success();
		}
	}

	@SubCommand ( name = "delentrada", aliases = { "deleteentrada", "rementrada", "removeentrada", "rementry", "deleteentry" }, usage = "entrada <evento> <id> ", description = "Remove uma entrada" )
	private CommandResult del ( final CommandSource src, final CommandArgs args )
	{
		if ( args.size() == 2 )
		{
			final String name = Framework.normalize( args.get( 0 ).toLowerCase() );
			final Optional< Event > event = EventoZero.getEventHandler().getEventByName( name );
			if ( event.isPresent() )
			{
				try
				{
					final int id = Integer.parseInt( args.get( 1 ) );
					final Event e = event.get();
					if ( e.getEntradas().get( id ) != null )
					{
						e.getEntradas().remove( id );
						src.sendMessage( "�9Entrada removida com sucesso." );
						return CommandResult.success();
					}
					else
					{
						src.sendMessage( "�cN�o existe nenhuma entrada com este ID. Considere listar as entradas usando /ez entradas <evento>." );
						return CommandResult.success();
					}
				}
				catch ( final NumberFormatException nfe )
				{
					src.sendMessage( "�cO ID precisa ser um n�mero." );
					return CommandResult.success();
				}
			}
			else
			{
				MessageHandler.EVENTO_NAO_ENCONTRADO.send( src.toCommandSender() );
				return CommandResult.success();
			}
		}
		else
		{
			final String s = String.format( "�9Utilize o comando �f/ez %s %s �9para �6%s�9.", "delentrada", "<evento> <id>", "remover uma posi��o de entrada" );
			src.sendMessage( s );
			return CommandResult.success();
		}
	}

	@SubCommand ( name = "entradas", aliases = { "entrys", "listentry", "getentradas", "getentrys", "startposes" }, usage = "<evento>", description = "Obt�m as entradas e suas IDs" )
	private CommandResult entradas ( final CommandSource src, final CommandArgs args )
	{
		if ( args.size() == 1 )
		{
			final String name = Framework.normalize( args.get( 0 ).toLowerCase() );
			final Optional< Event > event = EventoZero.getEventHandler().getEventByName( name );
			if ( event.isPresent() )
			{
				if ( event.get().getEntradas().size() != 0 )
				{
					src.sendMessage( "�9Entradas dispon�vels:" );
					for ( int i = 0; i < event.get().getEntradas().size(); i ++ )
					{
						final Optional< Location > pos0 = Optional.of( event.get().getEntradas().get( i ) );
						if ( pos0.isPresent() && ( pos0.get().getWorld() != null ) && ( Bukkit.getWorld( pos0.get().getWorld().getName() ) != null ) )
						{
							final Location pos = pos0.get();
							src.sendMessage( String.format( "�9ID �f= �6%s �f� �9%s: �f%s�9, %s: �f%s�9, %s: �f%s�9, %s: �f%s", "X", "World", i, pos.getWorld().getName(), pos.getBlockX(), "Y", pos.getBlockY(), "Z", pos.getBlockZ() ) );
							continue;
						}
						else
						{
							event.get().getEntradas().remove( i ); // remove caso a entrada esteja bugada (mundo nao existe mais, ou Location � nulo)
							continue;
						}
					}

					return CommandResult.success();
				}
				else
				{
					src.sendMessage( "�cNenhuma entrada definida para o evento." );
					return CommandResult.success();
				}
			}
			else
			{
				MessageHandler.EVENTO_NAO_ENCONTRADO.send( src.toCommandSender() );
				return CommandResult.success();
			}
		}
		else
		{
			final String s = String.format( "�9Utilize o comando �f/ez %s %s �9para �6%s�9.", "entradas", "<evento>", "listar entradas existentes" );
			src.sendMessage( s );
			return CommandResult.success();
		}
	}

	@SubCommand ( name = "addcamarote", aliases = { "newcamarote", "ncam", "addcam", "setcamarote" }, usage = "<evento> ", description = "Adiciona um novo camarote" )
	private CommandResult camarote ( final CommandSource src, final CommandArgs args )
	{
		if ( ( args.size() > 0 ) && ( args.size() < 3 ) )
		{
			final String name = Framework.normalize( args.get( 0 ).toLowerCase() );
			final Optional< Event > event = EventoZero.getEventHandler().getEventByName( name );
			if ( event.isPresent() )
			{
				String camarote = "{default}";
				if ( args.size() == 2 )
				{
					camarote = Framework.normalize( args.get( 1 ).toLowerCase() );
				}

				final Event e = event.get();
				e.getCamarotes().put( camarote, ( ( Player ) src.toCommandSender() ).getLocation() );
				if ( e.getCamarotes().containsKey( camarote ) )
				{
					if ( camarote.equals( "{default}" ) )
					{
						src.sendMessage( "�9O camarote padr�o foi atualizado." );
					}
					else
					{
						src.sendMessage( "�9O camarote �f" + camarote + "�9 foi atualizado." );
					}

					return CommandResult.success();
				}
				else
				{
					if ( camarote.equals( "{default}" ) )
					{
						src.sendMessage( "�9O camarote padr�o foi definido." );
					}
					else
					{
						src.sendMessage( "�9O camarote �f" + camarote + "�9 foi definido." );
					}

					return CommandResult.success();
				}
			}
			else
			{
				MessageHandler.EVENTO_NAO_ENCONTRADO.send( src.toCommandSender() );
				return CommandResult.success();
			}
		}
		else
		{
			final String s = String.format( "�9Utilize o comando �f/ez %s %s �9para �6%s�9.", "addcamarote", "<evento> <nome>", "listar entradas existentes" );
			src.sendMessage( s );
			return CommandResult.success();
		}
	}

	@SubCommand ( name = "pvp", usage = "<evento> <allow|deny> ", description = "Define o status de pvp" )
	private CommandResult pvp ( final CommandSource src, final CommandArgs args )
	{
		if ( args.size() == 2 )
		{
			final String name = Framework.normalize( args.get( 0 ).toLowerCase() );
			final Optional< Event > event = EventoZero.getEventHandler().getEventByName( name );
			if ( event.isPresent() )
			{
				final String set = args.get( 0 );
				final boolean bool = Framework.tryBoolean( set );
				if ( bool )
				{
					final boolean allows = Framework.getBoolean( set );
					if ( ! allows )
					{
						event.get().getFlags().addFlag( Flag.DISABLE_PVP );
						src.sendMessage( String.format( "�9PvP foi definido como bloqueado no evento %s", event.get().getName() ) );
					}
					else
					{
						src.sendMessage( String.format( "�9PvP foi definido como permitido no evento %s", event.get().getName() ) );
						event.get().getFlags().removeFlag( Flag.DISABLE_PVP );
					}
				}
				else
				{
					src.sendMessage( String.format( "�cSintaxe '%s' n�o � v�lida.", set ) );
					src.sendMessage( "�cUtilize 'allow' ou 'deny'" );
				}
			}
		}

		return CommandResult.success();
	}

	@SubCommand ( name = "mc", usage = "<evento> <allow|deny> ", description = "Bloqueia ou permite MC" )
	private CommandResult mc ( final CommandSource src, final CommandArgs args )
	{
		if ( args.size() == 2 )
		{
			final String name = Framework.normalize( args.get( 0 ).toLowerCase() );
			final Optional< Event > event = EventoZero.getEventHandler().getEventByName( name );
			if ( event.isPresent() )
			{
				final String set = args.get( 0 );
				final boolean bool = Framework.tryBoolean( set );
				if ( bool )
				{
					final boolean allows = Framework.getBoolean( set );
					if ( ! allows )
					{
						event.get().getFlags().addFlag( Flag.DISABLE_MC );
						src.sendMessage( String.format( "�9Tapete M�gico foi definido como bloqueado no evento %s", event.get().getName() ) );
					}
					else
					{
						src.sendMessage( String.format( "�9Tapete M�gico foi definido como permitido no evento %s", event.get().getName() ) );
						event.get().getFlags().removeFlag( Flag.DISABLE_MC );
					}
				}
				else
				{
					src.sendMessage( String.format( "�cSintaxe '%s' n�o � v�lida.", set ) );
					src.sendMessage( "�cUtilize 'allow' ou 'deny'" );
				}
			}
		}

		return CommandResult.success();
	}

	@SubCommand ( name = "tools", usage = "", description = "Abre a caixa de ferramentas do eventozero" )
	private CommandResult tools ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "cmd", usage = "", description = "comandos" )
	private CommandResult cmd ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "potion", usage = "", description = "po��es" )
	private CommandResult potion ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "kit", usage = "", description = "kits" )
	private CommandResult kit ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "party", usage = "", description = "party" )
	private CommandResult party ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "max", usage = "<evento> <quantia> ", description = "Define o n�mero m�ximo de jogadores (autoinicia)" )
	private CommandResult max ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "min", usage = "<evento> <quantia> ", description = "Define o n�mero m�nimo de jogadores para poder iniciar o evento" )
	private CommandResult min ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "autostop", usage = "<evento> <tempo> ", description = "Define o tempo para terminar o evento se n�o houver ganhadores." )
	private CommandResult autostop ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "setengine", usage = "<evento> <arquivo.sc> ", description = "Definir uma engine para um evento" )
	private CommandResult setengine ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "placements", usage = "<evento> <quantia> ", description = "Define o limite de coloca��es" )
	private CommandResult placements ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "points", usage = "", description = "pontos" )
	private CommandResult points ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "updates", usage = "", description = "Apenas verifica se h� novos updates" )
	private CommandResult updates ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "upgrade", usage = "", description = "Verifica e aplica o update automaticamente se existir" )
	private CommandResult upgrade ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	/**
	 * N�o pode haver reload enquanto algum evento estiver ocorrendo, aberto, etc.
	 * O reload faz unload de todos os eventos, desregistra todos os listeners, schedulers e afins,
	 * recarrega todas as configura��es, e ativa novamente o plugin.
	 *
	 * @param src
	 * @param args
	 * @return
	 */
	@SubCommand ( name = "reload", usage = "", description = "Faz reload completo no plugin" )
	private CommandResult reload ( final CommandSource src, final CommandArgs args )
	{
		return CommandResult.success();
	}

	@SubCommand ( name = "disable", usage = "", description = "Desativa o plugin completamente" )
	private CommandResult disable ( final CommandSource src, final CommandArgs args )
	{
		Bukkit.getPluginManager().disablePlugin( EventoZero.getInstance() );
		src.sendMessage( "�cPlugin desativado com �xito. :)" );
		return CommandResult.success();
	}
}
