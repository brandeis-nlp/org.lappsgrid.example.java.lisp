;;; This function simply returns the string Hello World that is in quotes.


(defun concat-strings (list)
  (apply #'concatenate 'string
         (remove-if-not #'stringp list)))

(DEFUN HELLOWORLD (WHO)
                  (concat-strings (list "HI" WHO "WORLD")))

(HELLOWORLD "Yours")