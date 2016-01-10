-- id | jogador | evento | vitorias | derrotas | dc | mortes
CREATE TABLE IF NOT EXISTS `{rankings}`
(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `jogador` TEXT,
  `evento` TEXT,
  `vitorias` INT NOT NULL DEFAULT '0',poha
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

-- id | tipo | evento | index | mundo | localização
CREATE TABLE IF NOT EXISTS `{signs}`
(
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `tipo` INT NOT NULL DEFAULT 1,
  `evento` TEXT,
  `mundo` TEXT,
  `index` INT NOT NULL DEFAULT 1,
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

-- id | jogador | evento| devolvido | vida | comida | xp | level | localizacao | itens | armadura
CREATE TABLE IF NOT EXISTS `{backup}`
(
  `id` INT NOT NULL AUTO_INCREMENT,
  `jogador` TEXT,
  `evento` TEXT,
  `devolvido` BOOLEAN NOT NULL DEFAULT FALSE,
  `vida` INTEGER NOT NULL DEFAULT '20',
  `comida` INTEGER NOT NULL DEFAULT '20',
  `xp` LONG NOT NULL DEFAULT '0',
  `level` LONG NOT NULL DEFAULT '0',
  `localizacao` TEXT,
  `itens` TEXT,
  `armadura` TEXT,
  PRIMARY KEY(`id`)
)
