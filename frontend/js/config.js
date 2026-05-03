// js/config.js

const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';

export const CONFIG = {
    // Detecta se deve usar o backend local ou o do Render
    API_URL: isLocalhost 
        ? 'http://localhost:8080' 
        : 'https://seu-projeto-backend.onrender.com',
    SUPABASE_URL: 'https://logixvrbnxyuklwwouad.supabase.co/',
    SUPABASE_KEY: 'sb_publishable_hTOjl9pkgv_KwUiOMxw3mA_LY9wxc4c'
};