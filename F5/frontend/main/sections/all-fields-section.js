window.registerDashboardSection({
    section: 'all-fields',
    label: 'Ver Canchas',
    sectionId: 'section-all-fields',
    html: '../../fields/html/all-fields.html',
    js: [
        '../../common/js/constants.js',
        '../../fields/js/grounds.js',
        '../../common/js/zones.js',
        '../../common/js/utils.js',
        '../../fields/js/all-fields.js'
    ],
    init: 'initAllFields',
    dynamicSubsections: [
        {   
            key: (field) => `field-${field.name.replaceAll(' ', '_')}`,
            label: (field) => field && field.name ? field.name : '',
            sectionId: 'section-field-details',
            html: '../../fields/html/field-details.html',
            js: [
                '../../common/js/constants.js',
                '../../fields/js/grounds.js',
                '../../fields/js/days.js',
                '../../common/js/zones.js',
                '../../common/js/utils.js',
                '../../fields/js/field-details.js'
            ],
            init: 'initFieldDetails'
        }
    ]
}); 