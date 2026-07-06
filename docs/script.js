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

    // --- 3. SHOWCASE CAROUSEL LOGIC (TAB 2) ---
    const track = document.querySelector('.carousel-track');
    const slides = Array.from(document.querySelectorAll('.carousel-slide'));
    const nextButton = document.getElementById('carousel-next');
    const prevButton = document.getElementById('carousel-prev');
    const dots = Array.from(document.querySelectorAll('.carousel-dot'));

    let currentSlideIdx = 0;
    let autoPlayTimer = null;

    function updateCarousel() {
        if (!track) return;
        track.style.transform = `translateX(-${currentSlideIdx * 100}%)`;

        slides.forEach((slide, idx) => {
            if (idx === currentSlideIdx) {
                slide.classList.add('active');
            } else {
                slide.classList.remove('active');
            }
        });

        dots.forEach((dot, idx) => {
            if (idx === currentSlideIdx) {
                dot.classList.add('active');
            } else {
                dot.classList.remove('active');
            }
        });
    }

    function nextSlide() {
        currentSlideIdx = (currentSlideIdx + 1) % slides.length;
        updateCarousel();
    }

    function prevSlide() {
        currentSlideIdx = (currentSlideIdx - 1 + slides.length) % slides.length;
        updateCarousel();
    }

    function startAutoPlay() {
        stopAutoPlay();
        autoPlayTimer = setInterval(nextSlide, 5000);
    }

    function stopAutoPlay() {
        if (autoPlayTimer) {
            clearInterval(autoPlayTimer);
            autoPlayTimer = null;
        }
    }

    if (track && slides.length > 0) {
        if (nextButton) {
            nextButton.addEventListener('click', () => {
                nextSlide();
                startAutoPlay();
            });
        }

        if (prevButton) {
            prevButton.addEventListener('click', () => {
                prevSlide();
                startAutoPlay();
            });
        }

        dots.forEach((dot, idx) => {
            dot.addEventListener('click', () => {
                currentSlideIdx = idx;
                updateCarousel();
                startAutoPlay();
            });
        });

        startAutoPlay();

        const wrapper = document.querySelector('.carousel-wrapper');
        if (wrapper) {
            wrapper.addEventListener('mouseenter', stopAutoPlay);
            wrapper.addEventListener('mouseleave', startAutoPlay);
        }
    }
});
