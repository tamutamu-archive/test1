case ${1} in
  run)
    sbt clean run
    exit 0
  ;;

  assembly|al)
    sbt clean assembly
    exit 0
  ;;

  dependencyTree|dt)
    sbt dependencyTree
    exit 0
  ;;

  slick_codegen|sc)
    sbt slick_codegen
    exit 0
  ;;

  *)
    echo "run|dependencyTree|assembly|slick_codegen"
  ;;
esac