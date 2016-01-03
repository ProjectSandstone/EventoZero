-- id | jogador | evento | vitorias | derrotas | dc | mortes
CREATE TABLE IF NOT EXISTS `{rankings}`
(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `jogador` TEXT,
  `evento` TEXT,
  `vitorias` INT NOT NULL DEFAULT '0',
  `derrotas` INT NOT NULL DEFAULT '0',
  `dc` INT NOT NULL DEFAULT '0',
  `mortes` INT NOT NULL DEFAULT '0',
  PRIMARY KEY(`id`)
)

-- id | jogador | evento | -OPCIONAL- motivo
CREATE TABLE IF NOT EXISTS `{bans}`
(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `jogador` TEXT,
  `evento` TEXT,
  `motivo` TEXT,
  PRIMARY KEY(`id`)
)

-- id | jogador | pontos
CREATE TABLE IF NOT EXISTS `{points}`
(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `jogador` TEXT,
  `pontos` LONG NOT NULL DEFAULT '0',
  PRIMARY KEY(`id`)
)

-- id | evento | mundo | localização
CREATE TABLE IF NOT EXISTS `{signs}`
(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `evento` TEXT,
  `mundo` TEXT,
  `localizacao` TEXT,
  PRIMARY KEY(`id`)
)

-- id | key | valor
CREATE TABLE IF NOT EXISTS `{others}`
(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `key` TEXT,
  `valor` OBJECT,
  PRIMARY KEY(`id`)
)