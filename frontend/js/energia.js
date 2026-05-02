const canvas = document.getElementById('energyCanvas');
const ctx = canvas.getContext('2d');

let particles = [];
const particleCount = 250;

function resize() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
}

window.addEventListener('resize', resize);
resize();

// Paleta extraída da imagem do Lúmen
const lumenColors = [
    '#FFD700', // Gold principal
    '#F0C419', // Amarelo vibrante
    '#4B3076', // Violeta profundo
    '#8A2BE2', // Azul violeta
    '#2D1B4B', // Roxo escuro (quase preto)
    '#FFFFFF'  // Brilho estelar branco
];

class Particle {
    constructor() {
        this.init();
    }

    init() {
        this.x = Math.random() * canvas.width;
        this.y = Math.random() * canvas.height;
        this.size = Math.random() * 2.5 + 0.5; // Tamanhos variados como poeira estelar
        this.color = lumenColors[Math.floor(Math.random() * lumenColors.length)];
        
        // Velocidade de movimento
        this.speedX = Math.random() * 0.4 + 0.1; 
        this.amplitude = Math.random() * 1.5 + 0.5; 
        this.frequency = Math.random() * 0.0015 + 0.0005; 
        this.offset = Math.random() * 2000;
        this.opacity = Math.random() * 0.6 + 0.1;
    }

    update() {
        this.x += this.speedX;
        // Ondulação suave para simular energia fluindo
        this.y += Math.sin(this.x * this.frequency + this.offset) * 0.5;

        // Reset infinito
        if (this.x > canvas.width + 10) {
            this.x = -10;
            this.y = Math.random() * canvas.height;
        }
    }

    draw() {
        ctx.globalAlpha = this.opacity;
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
        ctx.fillStyle = this.color;
        
        // Adiciona um brilho suave apenas nas partículas douradas e brancas
        if (this.color === '#FFD700' || this.color === '#FFFFFF') {
            ctx.shadowBlur = 8;
            ctx.shadowColor = this.color;
        } else {
            ctx.shadowBlur = 0;
        }
        
        ctx.fill();
    }
}

function init() {
    particles = [];
    for (let i = 0; i < particleCount; i++) {
        particles.push(new Particle());
    }
}

function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    particles.forEach(p => {
        p.update();
        p.draw();
    });
    
    requestAnimationFrame(animate);
}

init();
animate();