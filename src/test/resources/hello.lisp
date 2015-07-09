;;; This function simply returns the string Hello World that is in quotes.

(DEFUN hello (x)
                  x)

(DEFUN hello (x)
                  (format t "Hello ~A .~%" x))