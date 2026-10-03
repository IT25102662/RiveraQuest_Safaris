/* RiveraQuest Safaris - interactive touches (scroll reveal, count-up numbers, back-to-top).
   Everything here is optional decoration: if this file fails, the website still works. */
(function () {
    'use strict';
    const reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    /* ---------- 1. Scroll-in reveal for cards ---------- */
    const REVEAL_SELECTOR = '.grid > .glass-card, .grid > .feature-tile, .page-banner, .grid > button.glass-card';
    let revealObserver = null;
    if (!reduceMotion && 'IntersectionObserver' in window) {
        revealObserver = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('reveal-in');
                    revealObserver.unobserve(entry.target);
                }
            });
        }, { threshold: 0.08, rootMargin: '0px 0px -30px 0px' });
    }
    function prepareReveal(root) {
        if (!revealObserver || !root.querySelectorAll) return;
        const nodes = root.matches && root.matches(REVEAL_SELECTOR) ? [root] : [];
        root.querySelectorAll(REVEAL_SELECTOR).forEach((n) => nodes.push(n));
        nodes.forEach((el, i) => {
            if (el.dataset.revealReady) return;
            const rect = el.getBoundingClientRect();
            if (rect.width === 0) return;                 // page not shown yet: check again later
            el.dataset.revealReady = '1';
            // Only hide cards that are below the visible area; anything already visible stays visible.
            if (rect.top < window.innerHeight) return;
            el.classList.add('reveal');
            el.style.transitionDelay = Math.min(i % 6, 5) * 60 + 'ms';
            revealObserver.observe(el);
        });
    }
    // Every time a page is opened, its cards fade in again.
    function resetView(view) {
        view.querySelectorAll('[data-reveal-ready]').forEach((el) => {
            revealObserver.unobserve(el);
            el.classList.remove('reveal', 'reveal-in');
            el.style.transitionDelay = '';
            delete el.dataset.revealReady;
        });
        requestAnimationFrame(() => prepareReveal(view));
    }

    /* ---------- 2. Count-up numbers on dashboard statistics ---------- */
    const STAT_SELECTOR = '[id^="fleet-"], [id^="mkt-"], [id^="stat-"], [id^="saf-"], [id^="desk-"]';
    const NUMERIC = /^\d+(\.\d+)?$/;
    const animating = new WeakSet();
    function countUp(el) {
        const text = el.textContent.trim();
        if (!NUMERIC.test(text) || animating.has(el)) return;
        const target = parseFloat(text);
        if (!(target > 0)) return;
        const decimals = text.includes('.') ? text.split('.')[1].length : 0;
        animating.add(el);
        const duration = 1800;
        const start = performance.now();
        const frame = (now) => {
            const t = Math.min((now - start) / duration, 1);
            const eased = 1 - Math.pow(1 - t, 3);
            el.textContent = (target * eased).toFixed(decimals);
            if (t < 1) requestAnimationFrame(frame);
            else { el.textContent = target.toFixed(decimals); setTimeout(() => animating.delete(el), 50); }
        };
        requestAnimationFrame(frame);
    }
    const statObserver = new MutationObserver((mutations) => {
        if (reduceMotion) return;
        mutations.forEach((m) => {
            const el = m.target.nodeType === 3 ? m.target.parentElement : m.target;
            if (el && el.matches && el.matches(STAT_SELECTOR) && !animating.has(el)) countUp(el);
        });
    });
    function watchStats() {
        document.querySelectorAll(STAT_SELECTOR).forEach((el) => {
            statObserver.observe(el, { childList: true, characterData: true, subtree: true });
        });
    }

    /* ---------- 3. Back-to-top button ---------- */
    function addBackToTop() {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.id = 'back-to-top';
        btn.setAttribute('aria-label', 'Back to top');
        btn.innerHTML = '<span aria-hidden="true">&#8593;</span>';
        btn.addEventListener('click', () => window.scrollTo({ top: 0, behavior: reduceMotion ? 'auto' : 'smooth' }));
        document.body.appendChild(btn);
        let ticking = false;
        window.addEventListener('scroll', () => {
            if (ticking) return;
            ticking = true;
            requestAnimationFrame(() => {
                btn.classList.toggle('show', window.scrollY > 420);
                ticking = false;
            });
        }, { passive: true });
    }

    document.addEventListener('DOMContentLoaded', () => {
        try {
            addBackToTop();
            watchStats();
            prepareReveal(document.body);
            // Cards that the application draws later (lists, grids) are prepared as they appear.
            const main = document.querySelector('main') || document.body;
            let pending = false;
            new MutationObserver(() => {
                if (pending) return;
                pending = true;
                requestAnimationFrame(() => { pending = false; prepareReveal(main); });
            }).observe(main, { childList: true, subtree: true });
            if (revealObserver) {
                const pendingViews = new Set();
                new MutationObserver((records) => {
                    records.forEach((r) => {
                        const v = r.target;
                        if (v.classList && v.classList.contains('view-page') && !v.classList.contains('hidden')) pendingViews.add(v);
                    });
                    if (!pendingViews.size) return;
                    requestAnimationFrame(() => { pendingViews.forEach(resetView); pendingViews.clear(); });
                }).observe(main, { attributes: true, attributeFilter: ['class'], subtree: true });
            }
        } catch (e) { console.warn('Visual effects disabled:', e); }
    });
})();
