(ns embellir.illustrator.svgutils
  (:import 
     (java.awt Graphics2D RenderingHints)
     (java.awt.image BufferedImage)
     (java.io File)
     (org.apache.batik.transcoder TranscodingHints TranscoderInput TranscoderException)
     (org.apache.batik.transcoder.image ImageTranscoder)
     (org.apache.batik.dom.svg SVGDOMImplementation)
     (org.apache.batik.util SVGConstants)
     )
  )

(defn get-img-from-svg [file]
  (let [^java.awt.image.BufferedImage img (atom nil)
        css "svg {shape-rendering: geometricPrecision; text-rendering: geometricPrecision; color-rendering: optimizeQuality; image-rendering: optimizeQuality;}"
        cssfile (java.io.File/createTempFile "batik-default-override-" ".css")
        nada (with-open [w (clojure.java.io/writer cssfile)] (.write w css))
        transcoderHints (doto (new TranscodingHints) 
                (.put ImageTranscoder/KEY_XML_PARSER_VALIDATING, false)
                (.put ImageTranscoder/KEY_DOM_IMPLEMENTATION, (SVGDOMImplementation/getDOMImplementation ))
                (.put ImageTranscoder/KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants/SVG_NAMESPACE_URI)
                (.put ImageTranscoder/KEY_DOCUMENT_ELEMENT, "svg")
                (.put ImageTranscoder/KEY_USER_STYLESHEET_URI, (str (.toURI cssfile)))
                )
        ]

      (try
        (let [input (new TranscoderInput (clojure.java.io/input-stream (clojure.java.io/as-file file)))
              t (proxy [ImageTranscoder] []
                        (createImage [w h]
                          (new BufferedImage w h BufferedImage/TYPE_INT_ARGB))
                        (writeImage [image out]
                          (reset! img image)))                       
                         ]
              (.setTranscodingHints t transcoderHints)
              (.transcode t input, nil)
              @img
          )
        (catch TranscoderException ex (.printStackTrace ex) @img)
        (finally (.delete cssfile) )

        )

    )
  )
