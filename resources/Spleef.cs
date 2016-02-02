events:
  onEventInit:
    - cuboid('chao') set bedrock
  onEventStart:
    - message(*) '&7Transformando Bedrock em 5 segundos!!'
    - wait 5s
    - cuboid('chao') set snow
    - message(*) '&eBoa sorte!!'
    - task('1') start
  onPlayerMove:
    - cuboid('chao') point(y) > playerpoint(y) continue(true)
    - lose
  onPlayerQuit:
    - message(*) '&7Jogador {player} deslogou!!'
    - lose
  onPlayerJoinEvent:
    - item('pa') give check

tasks:
  1:
    autostart: false
    startafter: 30s
    interval: 30s
    run:
      - cuboid('chao') set snow

cuboids:
  chao:
    min: "World [world] X [1] Y [5] Z [10]"
    max: "World [world] X [10] Y [5] Z [-15]"

items:
  pa: 'nome{"&5Pá do Spleef"} item(DIAMOND_SPADE:0) lore("&cClique com botão direito mirando no bloco!")'