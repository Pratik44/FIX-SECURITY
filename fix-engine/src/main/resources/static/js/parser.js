/**
 * FIX Security - FIX Message Parser UI
 */

document.addEventListener('DOMContentLoaded', () => {
    const fixInput = document.getElementById('fixInput');
    const parseBtn = document.getElementById('parseBtn');
    const clearBtn = document.getElementById('clearBtn');
    const parseResult = document.getElementById('parseResult');
    const parseError = document.getElementById('parseError');
    const parseErrorMessage = document.getElementById('parseErrorMessage');

    if (!fixInput || !parseBtn) return;

    parseBtn.addEventListener('click', async () => {
        const raw = fixInput.value?.trim();
        parseError.hidden = true;

        if (!raw) {
            parseResult.innerHTML = '<p class="parse-placeholder">Enter a FIX message and click Parse.</p>';
            return;
        }

        parseBtn.disabled = true;
        try {
            const res = await fetch('/api/parse', {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' },
                body: raw
            });
            const data = await res.json();

            if (!res.ok) {
                parseError.hidden = false;
                parseErrorMessage.textContent = data.error || 'Parse failed';
                return;
            }

            parseResult.innerHTML = formatJson(data);
        } catch (err) {
            parseError.hidden = false;
            parseErrorMessage.textContent = 'Request failed: ' + err.message;
        } finally {
            parseBtn.disabled = false;
        }
    });

    clearBtn?.addEventListener('click', () => {
        fixInput.value = '';
        parseResult.innerHTML = '<p class="parse-placeholder">Enter a FIX message and click Parse.</p>';
        parseError.hidden = true;
    });

    function formatJson(obj) {
        const raw = JSON.stringify(obj, null, 2);
        return raw
            .replace(/"([^"\\]*(?:\\.[^"\\]*)*)":/g, (_, k) => '<span class="key">"' + escapeHtml(k) + '"</span>:')
            .replace(/: "((?:[^"\\]|\\.)*)"/g, (_, v) => ': <span class="string">"' + escapeHtml(v) + '"</span>')
            .replace(/: (-?\d+\.?\d*)/g, ': <span class="number">$1</span>')
            .replace(/: (true|false|null)/g, ': <span class="number">$1</span>');
    }

    function escapeHtml(s) {
        if (s == null) return '';
        const div = document.createElement('div');
        div.textContent = String(s);
        return div.innerHTML;
    }
});
