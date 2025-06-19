export async function loadSection(sectionId, url, jsUrls, initFn) {
    const section = document.getElementById(sectionId);
    if (!section) return;

    try {
        section.innerHTML = '';

        const response = await fetch(url);
        if (!response.ok) throw new Error('Error al cargar el HTML');
        section.innerHTML = await response.text();

        if (jsUrls) {
            if (typeof jsUrls === 'string') jsUrls = [jsUrls];

            for (const src of jsUrls) {
                if (!document.querySelector(`script[src="${src}"]`)) {
                    await new Promise((resolve, reject) => {
                        const script = document.createElement('script');
                        script.type = 'module';
                        script.src = src;
                        script.onload = resolve;
                        script.onerror = reject;
                        document.body.appendChild(script);
                    });
                }
            }
        }

        if (initFn) {
            const maxAttempts = 50;
            let attempts = 0;

            const tryInit = () => {
                if (typeof window[initFn] === 'function') {
                    window[initFn]();
                } else if (attempts < maxAttempts) {
                    attempts++;
                    setTimeout(tryInit, 100);
                }
            };

            tryInit();
        }
    } catch (error) {
        console.error('Error al cargar la sección:', error);
        section.innerHTML = '<div class="error-message">Error al cargar la sección. Por favor, intente nuevamente.</div>';
    }
}

window.loadSection = loadSection;
