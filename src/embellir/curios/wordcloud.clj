(ns embellir.curios.wordcloud
  (:require [clj-time.local :only local-now])
  )


(defn setup-wordcloud
  "return distribution of Scrabble tiles by default"
  []
  {:wordcloud {:lastchange (clj-time.local/local-now) :data { "A" 9, "B" 2, "C" 2, "D" 4, "E" 12, "F" 2, "G" 3, "H" 2, "I" 9, "J" 1, "K" 1, "L" 4, "M" 2, "N" 6, "O" 8, "P" 2, "Q" 1, "R" 6, "S" 4, "T" 6, "U" 4, "V" 2, "W" 2, "X" 1, "Y" 2, "Z" 1, "Blank" 2, }}}
  )

(defn receive-wordcloud
    [wdata rmap] ;rmap is {:keyname {:lastchange ... :data {...}}} and add in :lastchange with current timestamp
  (let [keyname (keyword (first (keys rmap)))]
    (merge wdata (assoc-in rmap [keyname :lastchange] (clj-time.local/local-now)))
    
    )
  
  )


(defn curation-map [] {:atom (atom (embellir.curios.wordcloud/setup-wordcloud))
                       :function nil
                       :time-to-live 0
                       :receiver-function embellir.curios.wordcloud/receive-wordcloud })


(comment (embellir.curator/curate "wordcloud"))
(comment (embellir.curator/get-curio "wordcloud"))
(comment (:message (embellir.curator/get-curio "wordcloud")))
(comment (embellir.curator/trash-curio "wordcloud"))


