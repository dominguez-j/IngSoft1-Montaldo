window.registerDashboardSection({
    section: 'my-matches',
    label: 'Mis partidos',
    subsections: [
      {
        key: 'current-matches',
        label: 'Partidos actuales',
        sectionId: 'section-current-matches',
        html: '../../matches/html/current-matches.html',
        js: [
          '../../common/js/constants.js',
          '../../common/js/utils.js',
          '../../matches/js/current-matches.js'
        ],
        init: 'initCurrentMatches'
      },
      {
        key: 'past-matches',
        label: 'Partidos pasados',
        sectionId: 'section-past-matches',
        html: '../../matches/html/past-matches.html',
        js: [
          '../../common/js/constants.js',
          '../../common/js/utils.js',
          '../../matches/js/past-matches.js'
        ],
        init: 'initPastMatches'
      },
      {
        key: 'organize-teams-matches',
        label: 'Organizar equipos',
        sectionId: 'section-organize-teams-matches',
        html: '../../matches/html/organize-teams-matches.html',
        js: [
          '../../common/js/constants.js',
          '../../common/js/utils.js',
          '../../matches/js/organize-teams-matches.js'
        ],
        init: 'initOrganizeTeamsMatches'
      }
    ]
  }); 