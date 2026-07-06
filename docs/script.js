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

    // --- 3. INTERACTIVE "WHO'S THAT POKEMON?" TRIVIA GAME (TAB 2) ---
    const triviaQuestions = [
        {
            filename: "tyrunt_pokedoll.geo.json",
            options: ["Tyrunt Pokedoll", "tyrunt_pokedoll", "Shiny Tyrunt Pokedoll", "Tyrunt Statue"],
            answer: 0,
            hint: "Underscores become spaces and each word is capitalized automatically!"
        },
        {
            filename: "charizard_doll_shiny.png",
            options: ["Charizard Doll Shiny", "charizard_doll", "Shiny Charizard Doll", "Mega Charizard Statue"],
            answer: 2,
            hint: "When a _shiny.png texture is detected, the game prepends the 'Shiny ' prefix!"
        },
        {
            filename: "my_cool_statue.geo.json",
            options: ["my cool statue", "My_cool_statue", "My Cool Statue", "Cool Statue Pokedoll"],
            answer: 2,
            hint: "Every single word is capitalized, and underscores are stripped."
        },
        {
            filename: "ancient_fossil_statue.geo.json",
            options: ["Ancient Fossil", "Ancient fossil statue", "ancient_fossil_statue", "Ancient Fossil Statue"],
            answer: 3,
            hint: "All three words begin with capital letters in the formatted display name."
        },
        {
            filename: "mewtwo_pokedoll_shiny.png",
            options: ["Mewtwo Pokedoll", "Shiny Mewtwo Pokedoll", "Mewtwo Shiny", "Shiny Mew Statue"],
            answer: 1,
            hint: "Notice the _shiny suffix at the end! It gets the shiny prefix."
        },
        {
            filename: "pikachu_doll.geo.json",
            options: ["Pikachu Doll", "Pikachu_doll", "Pika Statue", "Shiny Pikachu Doll"],
            answer: 0,
            hint: "Clean standard base model name without any shiny prefix."
        }
    ];

    let currentQIndex = 0;
    let score = 0;
    let hasAnswered = false;

    const filenameEl = document.getElementById('trivia-filename');
    const optionsEl = document.getElementById('trivia-options');
    const feedbackEl = document.getElementById('trivia-feedback');
    const scoreEl = document.getElementById('trivia-score');
    const totalEl = document.getElementById('trivia-total');
    const nextBtn = document.getElementById('btn-next-question');

    if (totalEl) totalEl.textContent = triviaQuestions.length;

    function loadQuestion() {
        hasAnswered = false;
        const q = triviaQuestions[currentQIndex];
        
        if (filenameEl) {
            filenameEl.style.transform = 'scale(0.9)';
            setTimeout(() => {
                filenameEl.textContent = q.filename;
                filenameEl.style.transform = 'scale(1)';
            }, 150);
        }

        if (feedbackEl) {
            feedbackEl.textContent = '';
            feedbackEl.style.color = '';
        }

        if (optionsEl) {
            optionsEl.innerHTML = '';
            q.options.forEach((optText, idx) => {
                const btn = document.createElement('button');
                btn.className = 'btn-option';
                btn.textContent = optText;
                btn.addEventListener('click', () => handleAnswer(idx, btn));
                optionsEl.appendChild(btn);
            });
        }
    }

    function handleAnswer(selectedIndex, clickedBtn) {
        if (hasAnswered) return;
        hasAnswered = true;

        const q = triviaQuestions[currentQIndex];
        const allOptionBtns = optionsEl.querySelectorAll('.btn-option');

        allOptionBtns.forEach((btn, idx) => {
            btn.classList.add('disabled');
            if (idx === q.answer) {
                btn.classList.add('correct');
            }
        });

        if (selectedIndex === q.answer) {
            score++;
            if (scoreEl) scoreEl.textContent = score;
            if (feedbackEl) {
                feedbackEl.textContent = "🎉 Correct! " + q.hint;
                feedbackEl.style.color = "#50fa7b";
            }
        } else {
            clickedBtn.classList.add('wrong');
            if (feedbackEl) {
                feedbackEl.textContent = "❌ Incorrect! Correct answer was: \"" + q.options[q.answer] + "\". " + q.hint;
                feedbackEl.style.color = "#ff5555";
            }
        }
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            currentQIndex = (currentQIndex + 1) % triviaQuestions.length;
            if (currentQIndex === 0 && hasAnswered) {
                // Reset quiz if completed
                score = 0;
                if (scoreEl) scoreEl.textContent = score;
            }
            loadQuestion();
        });
    }

    // Initialize trivia on startup
    if (filenameEl && optionsEl) {
        loadQuestion();
    }
});
