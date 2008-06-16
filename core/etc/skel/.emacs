
;; load lapiota configure
(defun lapiota-config-get ()
  (concat (getenv "LAPIOTA") "/lib/lisp/emacs/config.el"))

(defun lapiota-config-load ()
  (setq file (lapiota-config-get))
  (if (file-exists-p file)
      (load file)))

(defun lapiota-config-edit ()
  (setq file (lapiota-config-get))
  (if (file-exists-p file)
      (find-file file)))

(lapiota-config-load)

;; user extensions go here
;;
