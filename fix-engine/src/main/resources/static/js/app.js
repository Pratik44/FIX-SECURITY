/**
 * FIX Security Dashboard - Analytics & API status
 */

const API_BASE = '';

async function checkApiStatus() {
    const el = document.getElementById('apiStatus');
    if (!el) return;
    try {
        const res = await fetch(`${API_BASE}/health`);
        if (res.ok) {
            el.className = 'header-status healthy';
            el.querySelector('span:last-child').textContent = 'Connected';
        } else {
            el.className = 'header-status error';
            el.querySelector('span:last-child').textContent = 'Error';
        }
    } catch (err) {
        el.className = 'header-status error';
        el.querySelector('span:last-child').textContent = 'Offline';
    }
}

// Run on load for any page
document.addEventListener('DOMContentLoaded', () => {
    checkApiStatus();
    setInterval(checkApiStatus, 30000);
});

// Analytics dashboard logic (only on index page)
if (document.getElementById('anomaliesBody')) {
    let currentPeriod = 'day';
    let currentReport = 'anomalies';

    const periodBtns = document.querySelectorAll('.period-btn');
    const reportBtns = document.querySelectorAll('.report-btn');
    const refreshBtn = document.getElementById('refreshBtn');
    const loadingEl = document.getElementById('loading');
    const errorEl = document.getElementById('error');
    const periodLabel = document.getElementById('periodLabel');
    const recordCount = document.getElementById('recordCount');
    const anomaliesBody = document.getElementById('anomaliesBody');
    const orderIdsBody = document.getElementById('orderIdsBody');
    const anomaliesPanel = document.getElementById('anomaliesPanel');
    const orderIdsPanel = document.getElementById('orderIdsPanel');

    function showLoading(show) {
        loadingEl?.classList.toggle('visible', show);
        refreshBtn.disabled = show;
    }

    function showError(msg) {
        if (errorEl) {
            errorEl.hidden = !msg;
            if (msg) errorEl.querySelector('#errorMessage').textContent = msg;
        }
    }

    function formatTime(iso) {
        try {
            const d = new Date(iso);
            return d.toLocaleString();
        } catch {
            return iso || '—';
        }
    }

    async function fetchAnomalies() {
        showLoading(true);
        showError(null);
        try {
            const res = await fetch(`${API_BASE}/api/analytics/reports/anomalies?period=${currentPeriod}&limit=1000`);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            const data = await res.json();
            periodLabel.textContent = data.period.charAt(0).toUpperCase() + data.period.slice(1);
            recordCount.textContent = data.count;

            if (!data.anomalies || data.anomalies.length === 0) {
                anomaliesBody.innerHTML = '<tr><td colspan="6" class="empty">No anomalies for this period.</td></tr>';
            } else {
                anomaliesBody.innerHTML = data.anomalies.map(a => `
                    <tr>
                        <td class="mono">${formatTime(a.createdAt)}</td>
                        <td><span class="badge badge-high">${escapeHtml(a.anomalyType)}</span></td>
                        <td>${escapeHtml(a.description)}</td>
                        <td class="mono">${escapeHtml(a.orderId || '—')}</td>
                        <td class="mono">${escapeHtml(a.clOrdID || '—')}</td>
                        <td class="mono">${escapeHtml(a.sessionId || '—')}</td>
                    </tr>
                `).join('');
            }
        } catch (err) {
            showError('Failed to load anomalies: ' + err.message);
        } finally {
            showLoading(false);
        }
    }

    async function fetchOrderIds() {
        showLoading(true);
        showError(null);
        try {
            const res = await fetch(`${API_BASE}/api/analytics/reports/order-ids?period=${currentPeriod}&limit=1000`);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            const data = await res.json();
            periodLabel.textContent = data.period.charAt(0).toUpperCase() + data.period.slice(1);
            recordCount.textContent = data.count;

            if (!data.orderIds || data.orderIds.length === 0) {
                orderIdsBody.innerHTML = '<tr><td colspan="3" class="empty">No order IDs for this period.</td></tr>';
            } else {
                orderIdsBody.innerHTML = data.orderIds.map((o, i) => `
                    <tr>
                        <td>${i + 1}</td>
                        <td class="mono">${escapeHtml(o.orderId)}</td>
                        <td><span class="badge badge-medium">${o.anomalyCount}</span></td>
                    </tr>
                `).join('');
            }
        } catch (err) {
            showError('Failed to load order IDs: ' + err.message);
        } finally {
            showLoading(false);
        }
    }

    function load() {
        if (currentReport === 'anomalies') fetchAnomalies();
        else fetchOrderIds();
    }

    function escapeHtml(s) {
        if (s == null) return '';
        const div = document.createElement('div');
        div.textContent = s;
        return div.innerHTML;
    }

    periodBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            periodBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentPeriod = btn.dataset.period;
            load();
        });
    });

    reportBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            reportBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentReport = btn.dataset.report;
            anomaliesPanel.classList.toggle('active', currentReport === 'anomalies');
            orderIdsPanel.classList.toggle('active', currentReport === 'order-ids');
            load();
        });
    });

    refreshBtn?.addEventListener('click', load);

    // Initial load
    load();
}
