(ns embellir.curator
  (:gen-class)
  (:require  )
  )

{:name "alphabetloop" 
 :atom "a" 
 :private-atom ["abcdefghijklmnopqrstuvwxyz" 1] 
 :time-to-live 1000
 :function (fn alphabetloop [pub-atom priv-atom] 
             (swap! priv-atom 
                    (fn alphabetloop [] 
                      [first priv-atom (mod (inc (second priv-atom)))])))}



