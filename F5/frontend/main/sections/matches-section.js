window.registerDashboardSection({
    section: 'matches',
    label: 'Partidos',
    subsections: [
      {
        key: 'create-open-match',
        label: 'Crear abierto',
        sectionId: 'section-create-open-match',
        html: '../../matches/html/create-open-match.html',
        js: [
          '../../common/js/constants.js',
          '../../common/js/utils.js',
          '../../fields/js/name-fields.js',
          '../../fields/js/free-slots.js',
          '../../matches/js/create-open-match.js'
        ],
        init: 'attachOpenMatchFormListener'
      },
      {
        key: 'create-closed-match',
        label: 'Crear cerrado',
        sectionId: 'section-create-closed-match',
        html: '../../matches/html/create-closed-match.html',
        js: [
          '../../common/js/constants.js',
          '../../common/js/utils.js',
          '../../fields/js/name-fields.js',
          '../../fields/js/free-slots.js',
          '../../matches/js/create-closed-match.js'
        ],
        init: 'attachClosedMatchFormListener'
      },
      {
        key: 'join-open-matches',
        label: 'Unirse a abierto',
        sectionId: 'section-join-open-matches',
        html: '../../matches/html/join-open-match.html',
        js: [
          '../../common/js/constants.js',
          '../../common/js/utils.js',
          '../../matches/js/fields.js',
          '../../matches/js/join-open-match.js'
        ],
        init: 'initOpenMatches'
      }
    ]
  }); 