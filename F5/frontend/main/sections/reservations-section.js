window.registerDashboardSection({
    section: 'reservations',
    label: 'Mis Reservas',
    subsections: [
      {
        key: 'current-reservations',
        label: 'Reservas Actuales',
        sectionId: 'section-current-reservations',
        html: '../../reservations/html/current-reservations.html',
        js: [
          '../../common/js/constants.js',
          '../../common/js/utils.js',
          '../../reservations/js/current-reservations.js'
        ],
        init: 'initCurrentReservations'
      },
      {
        key: 'past-reservations',
        label: 'Reservas Pasadas',
        sectionId: 'section-past-reservations',
        html: '../../reservations/html/past-reservations.html',
        js: [
          '../../common/js/constants.js',
          '../../common/js/utils.js',
          '../../reservations/js/past-reservations.js'
        ],
        init: 'initPastReservations'
      }
    ]
  }); 