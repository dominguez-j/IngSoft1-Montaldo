window.registerDashboardSection({
  section: 'fields',
  label: 'Mis Canchas',
  subsections: [
    {
      key: 'create-field',
      label: 'Crear cancha',
      sectionId: 'section-create-field',
      html: '../../fields/html/fields-create.html',
      js: [
        '../../common/js/constants.js',
        '../../fields/js/grounds.js',
        '../../common/js/zones.js',
        '../../common/js/utils.js',
        '../../fields/js/fields-create.js'
      ],
      init: 'attachFieldFormListener'
    },
    {
      key: 'list-fields',
      label: 'Listar canchas',
      sectionId: 'section-list-fields',
      html: '../../fields/html/fields-list.html',
      js: [
        '../../common/js/constants.js',
        '../../fields/js/grounds.js',
        '../../common/js/zones.js',
        '../../common/js/utils.js',
        '../../fields/js/fields-list.js'
      ],
      init: 'initFieldsList',
      forceReload: true
    },
    {
      key: 'scheduler-create',
      label: 'Crear horario',
      sectionId: 'section-scheduler-create',
      html: '../../fields/html/scheduler-create.html',
      js: [
        '../../common/js/constants.js',
        '../../fields/js/days.js',
        '../../fields/js/name-fields.js',
        '../../common/js/utils.js',
        '../../fields/js/scheduler-create.js'
      ],
      init: 'attachSchedulerFormListener'
    },
    {
      key: 'maintenances-create',
      label: 'Crear mantenimiento',
      sectionId: 'section-maintenances-create',
      html: '../../fields/html/maintenances-create.html',
      js: [
        '../../common/js/constants.js',
        '../../fields/js/free-slots.js',
        '../../fields/js/name-fields.js',
        '../../common/js/utils.js',
        '../../fields/js/maintenances-create.js'
      ],
      init: 'attachMaintenancesFormListener'
    },
    {
      key: 'reservations-on-my-fields',
      label: 'Reservas sobre mis canchas',
      sectionId: 'section-reservations-on-my-fields',
      html: '../../fields/html/reservations-on-my-fields.html',
      js: [
        '../../common/js/constants.js',
        '../../fields/js/grounds.js',
        '../../fields/js/name-fields.js',
        '../../common/js/utils.js',
        '../../fields/js/reservations-on-my-fields.js'
      ],
      init: 'initReservationsOnMyFields'
    }
  ]
}); 