val rpUrl = "https://dl.bintray.com/typesafe/instrumented-reactive-platform"
val rpVersion = "15v09p03i03"

resolvers += "typesafe-rp-mvn" at rpUrl
resolvers += Resolver.url("typesafe-rp-ivy", url(rpUrl))(Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.rp" % "sbt-typesafe-rp" % rpVersion)
