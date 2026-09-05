(function() {
    var content = document.querySelector('.markdown-body');
    var tocList = document.getElementById('toc-list');
    if (!content || !tocList) return;

    var headings = content.querySelectorAll('h1, h2, h3, h4, h5, h6');
    if (headings.length === 0) {
        tocList.parentNode.style.display = 'none';
        return;
    }

    var tocItems = [];
    var minLevel = 6;
    Array.prototype.forEach.call(headings, function(h) {
        var level = parseInt(h.tagName.charAt(1), 10);
        if (level < minLevel) minLevel = level;
    });

    Array.prototype.forEach.call(headings, function(h, idx) {
        var level = parseInt(h.tagName.charAt(1), 10);
        var text = h.textContent || h.innerText || '';
        if (!h.id) {
            h.id = 'heading-' + idx;
        }
        var li = document.createElement('li');
        li.className = 'toc-item level-' + (level - minLevel);
        var a = document.createElement('a');
        a.href = '#' + h.id;
        a.textContent = text;
        a.dataset.target = h.id;
        li.appendChild(a);
        tocList.appendChild(li);
        tocItems.push({ li: li, heading: h });
    });

    function updateActive() {
        var scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        var offset = (document.querySelector('.header') && document.querySelector('.header').offsetHeight || 64) + 24;
        var activeIndex = -1;
        for (var i = 0; i < tocItems.length; i++) {
            var top = tocItems[i].heading.getBoundingClientRect().top + scrollTop - offset;
            if (top <= scrollTop + offset) {
                activeIndex = i;
            } else {
                break;
            }
        }
        tocItems.forEach(function(item) {
            item.li.classList.remove('active');
        });
        if (activeIndex >= 0) {
            tocItems[activeIndex].li.classList.add('active');
        }
    }

    window.addEventListener('scroll', updateActive, { passive: true });
    updateActive();

    // code-prettify 代码高亮
    var pres = content.querySelectorAll('pre');
    Array.prototype.forEach.call(pres, function(pre) {
        pre.classList.add('prettyprint');
        var code = pre.querySelector('code');
        if (code) {
            code.classList.add('prettyprint');
        }
    });
    if (typeof PR !== 'undefined' && PR.prettyPrint) {
        PR.prettyPrint();
    }

    tocList.addEventListener('click', function(e) {
        var a = e.target.closest('a');
        if (!a) return;
        var target = document.getElementById(a.dataset.target);
        if (target) {
            e.preventDefault();
            var offset = (document.querySelector('.header') && document.querySelector('.header').offsetHeight || 64) + 16;
            var top = target.getBoundingClientRect().top + (window.pageYOffset || document.documentElement.scrollTop) - offset;
            window.scrollTo({ top: top, behavior: 'smooth' });
            history.replaceState(null, null, '#' + a.dataset.target);
        }
    });
})();

// 移动端：点击“打开菜单”按钮显示/收起左侧抽屉菜单
(function() {
    var navMore = document.querySelector('.nav-more');
    var sidebar = document.querySelector('.sidebar');
    var backdrop = document.getElementById('navBackdrop');
    if (!navMore || !sidebar) return;

    function setOpen(open) {
        sidebar.classList.toggle('open', open);
        if (backdrop) backdrop.classList.toggle('show', open);
    }

    navMore.addEventListener('click', function() {
        setOpen(!sidebar.classList.contains('open'));
    });

    if (backdrop) {
        backdrop.addEventListener('click', function() { setOpen(false); });
    }

    // 点击侧边栏链接后收起抽屉
    sidebar.addEventListener('click', function(e) {
        if (e.target.closest('a')) setOpen(false);
    });
})();