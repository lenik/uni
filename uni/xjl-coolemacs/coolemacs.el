
;; .section. application personalize

(custom-set-variables
  ;; custom-set-variables was added by Custom -- don't edit or cut/paste it!
  ;; Your init file should contain only one such instance.
 '(case-fold-search t)
 '(current-language-environment "Chinese-GB")
 '(default-input-method "chinese-py-punct")
 '(dired-dwim-target nil)
 '(dired-listing-switches "-aBGl")
 '(global-font-lock-mode t nil (font-lock))
 '(keyboard-coding-system (quote utf8))
 '(list-directory-verbose-switches "-l")
 '(fill-column 80)
 '(comment-column 40)
 '(scroll-step 4)
 )

(put 'downcase-region 'disabled nil)

(put 'upcase-region 'disabled nil)

(setq make-backup-files nil)
(setq auto-save-default t)

; (setq tab-stop-list '(16 32 48 64 80 96 112))
(setq-default indent-tabs-mode nil)
(setq default-tab-width 4)
(setq c-basic-offset 4)

(define-prefix-command 'keybinds)
(define-key keybinds [f3] "\C-x\e\e\C-a\C-k\C-g") ; get last command
(define-key keybinds [f1] 'local-set-key-echo)
(global-set-key [f3] 'keybinds)
(defun local-set-key-echo()
  (interactive)
  (call-interactively 'local-set-key)
  (kill-new (format "%s" (car command-history))))

(global-set-key "\C-m" 'newline-and-indent) ; auto-indent for <Enter>

(global-set-key "\M-l" 'goto-line)
(global-set-key [C-tab] 'other-window)
(global-set-key [C-f4] 'delete-window)
(global-set-key [C-S-f4] 'kill-buffer)
(global-set-key [?\C-\;] 'repeat)       ; control-;

(require 'session)
(add-hook 'after-init-hook 'session-initialize)

(load "desktop")
(desktop-load-default)
(desktop-read)
;; M-x desktop-save save the desktop

(require 'ibuffer)
(global-set-key "\C-x\C-b" 'ibuffer)

(require 'browse-kill-ring)
(global-set-key "\C-c k" 'browse-kill-ring)
(browse-kill-ring-default-keybindings)

(column-number-mode t)

(require 'ido)
(ido-mode t)

;(require 'swbuff)
;(global-set-key "\W-PgUp" 'swbuff-switch-to-previous-buffer)
;(global-set-key "\W-PgDn" 'swbuff-switch-to-next-buffer)
;(setq swbuff-exclude-buffer-regexps '("^ " "\\*.*\\*"))
;(setq swbuff-status-window-layout 'scroll)
;(setq swbuff-clear-delay 1)
;(setq swbuff-separator "|")
;(setq swbuff-window-min-text-height 1)

;(require 'tabbar)
;(tabbar-mode)
;(global-set-key "\W-up" 'tabbar-backward-group)
;(global-set-key "\W-down" 'tabbar-forward-group)
;(global-set-key "\W-left" 'tabbar-backward)
;(global-set-key "\W-right" 'tabbar-forward)

; speedbar - for navigate file system
(autoload 'table-insert "table" "simple table editor")

(require 'recentf)
(recentf-mode 1)
(defun recentf-open-files-compl()
  (interactive)
  (let* ((all-files recentf-list)
         (tocpl (mapcar (function
                         (lambda (x) (cons (file-name-nondirectory x) x))) all-files))
         (prompt (append '("File name: ") tocpl))
         (fname (completing-read (car prompt) (cdr prompt) nil nil)))
    (find-file (cdr (assoc-ignore-representation fname tocpl)))))
(global-set-key "\C-x\C-r" 'recentf-open-files-compl)

;; (require 'php-mode)
;; (require 'emacs-wiki)
;; (global-set-key [?\C-\M-\?] 'emacs-wiki-follow-name-at-point)

;; .section. lenik key bindings

(defun lenik-help()
  (interactive)
  (print "Lenik help is unavailable"))

(defun goto-previous-section()
  (interactive)
  (beginning-of-line)
  (if (search-backward ".section." nil t)
      ()
    (end-of-buffer)
    (search-backward ".section.")))

(defun goto-next-section()
  (interactive)
  (end-of-line)
  (if (search-forward ".section." nil t)
      ()
    (beginning-of-buffer)
    (search-forward ".section.")))

(define-prefix-command 'Lenik-Prefix)
(global-set-key "\C-c\C-l" 'Lenik-Prefix)
(define-key Lenik-Prefix "\C-h" 'lenik-help)
(define-key Lenik-Prefix "\C-p" 'goto-previous-section)
(define-key Lenik-Prefix "\C-n" 'goto-next-section)


;; initial frame (previously screen) layout
(set-frame-width  (selected-frame) 120)
(set-frame-height (selected-frame) 45)

;; (defun seq (&rest args) args)
(if recentf-list
    (list (find-file (car recentf-list))
          (split-window-horizontally 40)
          (find-file (file-name-directory (car recentf-list)))
          (other-window 1)))

;; .section. theme
(require 'color-theme)
; (color-theme-parus)
(color-theme-robin-hood)
