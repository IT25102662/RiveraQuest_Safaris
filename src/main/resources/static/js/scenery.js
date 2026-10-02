/**
 * Scenery - themed picture headers for every trip card.
 * Layer 1 (top):    your own photo        -> img/<kind>.jpg (optional)
 * Layer 2:          free stock photo (Pexels licence)
 * Layer 3 (bottom): built-in illustrated scene (offline fallback)
 * Any layer that fails to load is skipped automatically.
 */
const Scenery = {
    // Free-to-use Pexels photos (two per scene type; chosen by trip id so cards differ)
    photos: {
        river: [3845059, 37391954],
        whale: [17980729, 14834819],
        reef:  [8300146, 35993701],
        coast: [34714738, 35667401]
    },

    photoUrl(kind, trip) {
        const list = this.photos[kind];
        const id = list[(trip && trip.id ? trip.id : 0) % list.length];
        return `https://images.pexels.com/photos/${id}/pexels-photo-${id}.jpeg?auto=compress&cs=tinysrgb&w=800`;
    },

    kindOf(trip) {
        const r = trip && trip.route ? trip.route : {};
        const text = `${r.origin || ''} ${r.destination || ''} ${trip && trip.boat ? trip.boat.name : ''}`.toLowerCase();
        if (/whale|offshore|mirissa|dolphin|ocean/.test(text)) return 'whale';
        if (/pigeon|island|nilaveli|trinco|coral|reef/.test(text)) return 'reef';
        if (/madu|balapitiya|kothduwa|bentota|river|lagoon|mangrove/.test(text)) return 'river';
        return 'coast';
    },

    _boat(x, y, s) {
        return `<g transform="translate(${x} ${y}) scale(${s})"><path d="M0-34L0 0-26 0Z" fill="#fdefc8"/><path d="M4-28L4 0 28 0Z" fill="#f7c95a"/><path d="M-34 4H38L30 18Q28 21 24 21H-20Q-24 21-26 18Z" fill="#d9921a"/><path d="M-34 4H38" stroke="#fff" stroke-width="2"/></g>`;
    },

    _palm(x, y, s) {
        return `<g transform="translate(${x} ${y}) scale(${s})"><path d="M0 0Q4-30 2-60" stroke="#5b3a1a" stroke-width="4" fill="none"/><g fill="#0d645f"><path d="M2-60Q-24-66-38-48Q-16-56 2-60Z"/><path d="M2-60Q28-68 42-50Q20-58 2-60Z"/><path d="M2-60Q-10-84-30-84Q-12-76 2-60Z"/><path d="M2-60Q14-84 34-82Q16-74 2-60Z"/></g></g>`;
    },

    _svg(kind) {
        const W = 400, H = 220;
        let body = '';
        if (kind === 'river') {
            body = `<defs><linearGradient id="s" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#bfe8d8"/><stop offset="1" stop-color="#f3f1c9"/></linearGradient><linearGradient id="w" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#3f9a8a"/><stop offset="1" stop-color="#0c6a6a"/></linearGradient></defs>
            <rect width="${W}" height="${H}" fill="url(#s)"/><circle cx="300" cy="55" r="26" fill="#fff6c8" opacity=".9"/>
            <path d="M0 120Q60 80 120 112T250 100T400 118V150H0Z" fill="#5fae8f" opacity=".75"/>
            <rect y="130" width="${W}" height="90" fill="url(#w)"/>
            <g fill="#0f5c3f"><ellipse cx="30" cy="125" rx="60" ry="34"/><ellipse cx="110" cy="132" rx="46" ry="26"/><ellipse cx="370" cy="122" rx="62" ry="36"/><ellipse cx="300" cy="134" rx="40" ry="24"/></g>
            <g stroke="#3b2a14" stroke-width="3" opacity=".8"><path d="M20 150V132M45 150V134M95 152V138M360 150V134M330 152V138M290 152V140"/></g>
            <path d="M0 175Q50 168 100 175T200 175T300 175T400 175" stroke="#9fe0cf" stroke-width="2" fill="none" opacity=".6"/>${this._boat(205, 168, 1.05)}`;
        } else if (kind === 'whale') {
            body = `<defs><linearGradient id="s" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#f7c95a"/><stop offset=".45" stop-color="#8fd3e6"/><stop offset="1" stop-color="#2bbad3"/></linearGradient><linearGradient id="w" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#0b6b87"/><stop offset="1" stop-color="#061827"/></linearGradient></defs>
            <rect width="${W}" height="${H}" fill="url(#s)"/><circle cx="75" cy="85" r="28" fill="#fff3bd"/>
            <rect y="105" width="${W}" height="115" fill="url(#w)"/>
            <path d="M255 168Q300 120 350 150Q330 160 322 178Q300 170 255 168Z" fill="#244a63"/>
            <path d="M318 150Q328 118 346 112Q344 128 352 146" fill="#244a63"/>
            <g fill="#e8fbfd" opacity=".85"><circle cx="290" cy="128" r="4"/><circle cx="302" cy="118" r="3"/><circle cx="278" cy="120" r="3"/><circle cx="310" cy="132" r="3"/></g>
            <path d="M0 150Q50 140 100 150T200 150T300 150T400 150" stroke="#5fd3e4" stroke-width="2" fill="none" opacity=".55"/>
            <path d="M0 190Q50 180 100 190T200 190T300 190T400 190" stroke="#2bbad3" stroke-width="2" fill="none" opacity=".45"/>${this._boat(120, 168, 1.15)}`;
        } else if (kind === 'reef') {
            body = `<defs><linearGradient id="s" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#7fd6e8"/><stop offset="1" stop-color="#e8fbfd"/></linearGradient><linearGradient id="w" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#2ad0cf"/><stop offset="1" stop-color="#0a86a6"/></linearGradient></defs>
            <rect width="${W}" height="${H}" fill="url(#s)"/><circle cx="330" cy="50" r="24" fill="#fff6c8"/>
            <rect y="118" width="${W}" height="102" fill="url(#w)"/>
            <ellipse cx="110" cy="124" rx="78" ry="20" fill="#f1d58a"/><ellipse cx="110" cy="116" rx="52" ry="14" fill="#2f9a6b"/>${this._palm(95, 118, .7)}${this._palm(130, 120, .55)}
            <g fill="#ff9f43"><path d="M250 180q14-10 28 0q-14 10-28 0zM282 180l10-7v14z"/></g><g fill="#ffd166"><path d="M300 200q12-8 24 0q-12 8-24 0zM326 200l8-6v12z"/></g>
            <path d="M0 160Q50 150 100 160T200 160T300 160T400 160" stroke="#bff3f0" stroke-width="2" fill="none" opacity=".6"/>${this._boat(290, 150, .9)}`;
        } else {
            body = `<defs><linearGradient id="s" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#ffb457"/><stop offset=".6" stop-color="#ffe2a8"/><stop offset="1" stop-color="#fdefc8"/></linearGradient><linearGradient id="w" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#1a8cae"/><stop offset="1" stop-color="#0c2338"/></linearGradient></defs>
            <rect width="${W}" height="${H}" fill="url(#s)"/><circle cx="200" cy="112" r="34" fill="#fff2c2" opacity=".95"/>
            <rect y="120" width="${W}" height="100" fill="url(#w)"/>
            <path d="M0 150Q40 120 90 150V160H0Z" fill="#0c2338" opacity=".35"/>${this._palm(40, 150, .9)}${this._palm(350, 150, 1)}
            <path d="M0 165Q50 157 100 165T200 165T300 165T400 165" stroke="#f7c95a" stroke-width="2" fill="none" opacity=".6"/>${this._boat(215, 170, 1.05)}`;
        }
        const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${W} ${H}" preserveAspectRatio="xMidYMid slice">${body}</svg>`;
        return `url("data:image/svg+xml,${encodeURIComponent(svg)}")`;
    },

    /** Returns the HTML for a picture header. heightClass e.g. 'h-44'. */
    html(trip, heightClass = 'h-40', extraClass = '') {
        const kind = this.kindOf(trip);
        const bg = `url('img/${kind}.jpg'), url('${this.photoUrl(kind, trip)}'), ${this._svg(kind)}`;
        return `<div class="scenery scenery-${kind} ${heightClass} ${extraClass}" style="background-image:${bg.replace(/"/g, '&quot;')}"><span class="scenery-tag">${({river:'River & Mangrove',whale:'Whale Coast',reef:'Island & Reef',coast:'Coastal Cruise'})[kind]}</span></div>`;
    }
};
