window.registerDashboardSection({
  section: 'teams',
  label: 'Mis Equipos',
  subsections: [
    {
      key: 'create-team',
      label: 'Crear equipo',
      sectionId: 'section-create-team',
      html: '../../teams/html/teams-create.html',
      js: [
        '../../common/js/constants.js',
        '../../teams/js/ranks.js',
        '../../common/js/utils.js',
        '../../teams/js/teams-create.js'
      ],
      init: 'attachTeamFormListener'
    },
    {
      key: 'list-teams',
      label: 'Listar equipos',
      sectionId: 'section-list-teams',
      html: '../../teams/html/teams-list.html',
      js: [
        '../../common/js/constants.js',
        '../../common/js/utils.js',
        '../../teams/js/teams-list.js'
      ],
      init: 'initTeamsList'
    },
    {
      key: 'manage-players',
      label: 'Gestionar jugadores',
      sectionId: 'section-manage-players',
      html: '../../teams/html/manage-players.html',
      js: [
        '../../common/js/constants.js',
        '../../common/js/utils.js',
        '../../teams/js/name-teams.js',
        '../../teams/js/manage-players.js'
      ],
      init: 'initManagePlayers'
    }
  ]
}); 