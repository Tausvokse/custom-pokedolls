document.addEventListener('DOMContentLoaded', () => {
    // --- 1. TAB SWITCHING LOGIC ---
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetId = button.getAttribute('data-tab');

            // Remove active classes
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabPanes.forEach(pane => {
                pane.classList.remove('active');
                pane.style.opacity = '0';
                pane.style.transform = 'translateY(15px)';
            });

            // Activate clicked tab
            button.classList.add('active');
            const targetPane = document.getElementById(targetId);
            if (targetPane) {
                targetPane.classList.add('active');
                // Trigger reflow for smooth transition animation
                void targetPane.offsetWidth;
                targetPane.style.opacity = '1';
                targetPane.style.transform = 'translateY(0)';
            }

            // Smooth scroll to top of main content on tab change
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    });

    // --- 2. COPY TO CLIPBOARD BUTTONS ---
    const copyButtons = document.querySelectorAll('.btn-copy');
    copyButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const textToCopy = btn.getAttribute('data-copy');
            if (textToCopy) {
                navigator.clipboard.writeText(textToCopy).then(() => {
                    const originalText = btn.textContent;
                    btn.textContent = '✓ Copied!';
                    btn.style.background = '#50fa7b';
                    btn.style.color = '#000';
                    btn.style.borderColor = '#50fa7b';

                    setTimeout(() => {
                        btn.textContent = originalText;
                        btn.style.background = '';
                        btn.style.color = '';
                        btn.style.borderColor = '';
                    }, 2000);
                }).catch(err => {
                    console.error('Failed to copy text: ', err);
                });
            }
        });
    });

    // --- 3. INTERACTIVE "WHO'S THAT POKEMON?" SILHOUETTE GAME (TAB 2) ---
    const wtpRounds = [
        {
            icon: "🐭⚡",
            name: "Pikachu",
            options: ["Pikachu", "Raichu", "Jolteon", "Pichu"],
            answer: 0,
            hint: "P (7) - Electric Mouse Pokemon!"
        },
        {
            icon: "🔥🦎",
            name: "Charmander",
            options: ["Cyndaquil", "Charmander", "Charmeleon", "Vulpix"],
            answer: 1,
            hint: "C (10) - Has a flame burning on the tip of its tail!"
        },
        {
            icon: "🐢💧",
            name: "Squirtle",
            options: ["Totodile", "Psyduck", "Squirtle", "Blastoise"],
            answer: 2,
            hint: "S (8) - Tiny Turtle Pokemon that sprays water!"
        },
        {
            icon: "🐸🌿",
            name: "Bulbasaur",
            options: ["Chikorita", "Bulbasaur", "Oddish", "Treecko"],
            answer: 1,
            hint: "B (9) - Has a plant bulb growing on its back!"
        },
        {
            icon: "🐉🔥",
            name: "Charizard",
            options: ["Dragonite", "Aerodactyl", "Charizard", "Salamence"],
            answer: 2,
            hint: "C (9) - Breathes intense fire that melts boulders!"
        },
        {
            icon: "👻🌙",
            name: "Gengar",
            options: ["Gastly", "Gengar", "Haunter", "Mewtwo"],
            answer: 1,
            hint: "G (6) - Shadow Pokemon that hides in dark rooms!"
        }
    ];

    let currentRoundIdx = 0;
    let score = 0;
    let attempts = 3;
    let roundActive = true;

    const silhouetteEl = document.getElementById('wtp-silhouette');
    const optionsEl = document.getElementById('wtp-options');
    const feedbackEl = document.getElementById('wtp-feedback');
    const scoreEl = document.getElementById('wtp-score');
    const attemptsEl = document.getElementById('wtp-attempts');
    const nextBtn = document.getElementById('btn-wtp-next');
    const hintBtn = document.getElementById('btn-wtp-hint');

    function loadRound() {
        roundActive = true;
        attempts = 3;
        const q = wtpRounds[currentRoundIdx];
        
        if (silhouetteEl) {
            silhouetteEl.style.filter = 'brightness(0) drop-shadow(0 0 10px rgba(102, 252, 241, 0.4))';
            silhouetteEl.style.transform = 'scale(0.85)';
            setTimeout(() => {
                silhouetteEl.textContent = q.icon;
                silhouetteEl.style.transform = 'scale(1)';
            }, 150);
        }

        if (attemptsEl) attemptsEl.textContent = attempts;
        if (feedbackEl) {
            feedbackEl.textContent = 'Guesser Ready! Select the matching Pokemon below.';
            feedbackEl.style.color = 'var(--text-secondary)';
        }

        if (optionsEl) {
            optionsEl.innerHTML = '';
            q.options.forEach((optText, idx) => {
                const btn = document.createElement('button');
                btn.className = 'btn-option';
                btn.textContent = optText;
                btn.addEventListener('click', () => handleGuess(idx, btn));
                optionsEl.appendChild(btn);
            });
        }
    }

    function revealPokemon() {
        if (silhouetteEl) {
            silhouetteEl.style.filter = 'brightness(1) drop-shadow(0 0 25px rgba(255, 215, 0, 0.8))';
            silhouetteEl.style.transform = 'scale(1.15) rotate(-5deg)';
        }
    }

    function handleGuess(selectedIndex, clickedBtn) {
        if (!roundActive) return;
        const q = wtpRounds[currentRoundIdx];

        if (selectedIndex === q.answer) {
            roundActive = false;
            score += 50;
            if (scoreEl) scoreEl.textContent = score;
            revealPokemon();
            
            clickedBtn.classList.add('correct');
            const allBtns = optionsEl.querySelectorAll('.btn-option');
            allBtns.forEach(b => b.classList.add('disabled'));

            if (feedbackEl) {
                feedbackEl.textContent = "🎉 Correct! It's " + q.name + "! (+50 Points Awarded)";
                feedbackEl.style.color = "#50fa7b";
            }
        } else {
            attempts--;
            if (attemptsEl) attemptsEl.textContent = attempts;
            clickedBtn.classList.add('wrong', 'disabled');
            score = Math.max(0, score - 10);
            if (scoreEl) scoreEl.textContent = score;

            if (attempts <= 0) {
                roundActive = false;
                revealPokemon();
                const allBtns = optionsEl.querySelectorAll('.btn-option');
                allBtns.forEach((b, idx) => {
                    b.classList.add('disabled');
                    if (idx === q.answer) b.classList.add('correct');
                });
                if (feedbackEl) {
                    feedbackEl.textContent = "💥 Out of attempts! It was " + q.name + "! (-10 Points)";
                    feedbackEl.style.color = "#ff5555";
                }
            } else {
                if (feedbackEl) {
                    feedbackEl.textContent = "❌ Incorrect guess! " + attempts + " attempts remaining (-10 Points).";
                    feedbackEl.style.color = "#ffaa00";
                }
            }
        }
    }

    if (hintBtn) {
        hintBtn.addEventListener('click', () => {
            if (!roundActive) return;
            const q = wtpRounds[currentRoundIdx];
            score = Math.max(0, score - 15);
            if (scoreEl) scoreEl.textContent = score;
            if (feedbackEl) {
                feedbackEl.textContent = "💡 Hint revealed (-15 pts): " + q.hint;
                feedbackEl.style.color = "var(--accent-cyan)";
            }
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            currentRoundIdx = (currentRoundIdx + 1) % wtpRounds.length;
            loadRound();
        });
    }

    if (silhouetteEl && optionsEl) {
        loadRound();
    }
});
