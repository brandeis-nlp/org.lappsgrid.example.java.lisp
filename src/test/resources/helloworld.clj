;;; (defun helloworld ()
;;;    "Hello World!")
;;;
;;; (defun hello-world (whoes)
;;;     (concatenate 'string "Hello " whoes " World!"))

;;; helloworld.clj

;;; ================================================== ;;;
;;; =========== HELLO WORLD SIMULATION ============== ;;;
;;; ================================================== ;;;


;;; This function simply returns the string Hello World that is in quotes.

(defn hello [who] (str "Hello " who"!"))