export function getActiveSectionBySubsection(subsectionKey, fieldData) {
    return window.dashboardSections?.find(cfg =>
        (cfg.subsections && cfg.subsections.some(s => s.key === subsectionKey)) ||
        (cfg.dynamicSubsections && cfg.dynamicSubsections.some(ds => {
            const key = typeof ds.key === 'function' ? ds.key(fieldData) : ds.key;
            return key === subsectionKey;
        }))
    ) || null;
}

export function getSubsectionFromSection(section, subsectionKey, fieldData) {
    if (!section) return null;

    if (Array.isArray(section.dynamicSubsections)) {
        for (const sub of section.dynamicSubsections) {
            const key = typeof sub.key === 'function' ? sub.key(fieldData) : sub.key;
            if (key === subsectionKey) return { ...sub };
        }
    }

    if (Array.isArray(section.subsections)) {
        return section.subsections.find(s => s.key === subsectionKey) || null;
    }

    return null;
}

export function getSubsectionByKey(subsectionKey, fieldData) {
    const section = getActiveSectionBySubsection(subsectionKey, fieldData);
    return getSubsectionFromSection(section, subsectionKey, fieldData);
}