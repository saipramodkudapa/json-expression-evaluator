# json-expression-evaluator
validates an expression against an input json

# prerequisites
sbt 

# compiling 
sbt compile

# run 
sbt run 

sample input:
input expression -> ( $cost == 100.0 AND ( $mattress.big == false ) ) OR $size == 100
input json -> {"color":"red","size":10,"cost":100.0,"mattress":{"name":"king"},"big":true,"legs":[{"length":4}]}

Note: Avoid using "" in input expression.
