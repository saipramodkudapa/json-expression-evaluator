# json-expression-evaluator
- Scala based expression validator
- validates an expression against an input json

- sample input:

input expression -> ( $cost == 100.0 AND ( $mattress.big == false ) ) OR $size == 100
input json -> {"color":"red","size":10,"cost":100.0,"mattress":{"name":"king"},"big":true,"legs":[{"length":4}]}


# prerequisites
sbt
scala

# compiling 
sbt compile

# run 
sbt run 

Note: Avoid using "" in input expression.
