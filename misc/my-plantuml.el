(setq plantuml-view "~/bin/plantuml-view/bin/plantuml-view")

(defun plantuml-view()
  (interactive)
  (start-process "plantuml-view" 
		 nil "/bin/bash" (expand-file-name plantuml-view) (buffer-file-name)))


(provide 'my-plantuml)
