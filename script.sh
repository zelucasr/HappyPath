cd src/
javac ./*/*.java
mkdir ../tests/outputs

for f in ../tests/inputs/*; do
    entrada=`cat $f`
    java model.Grid $entrada > "../tests/outputs/$(basename "$f" .txt).output"
    errors=`diff "../tests/outputs/$(basename "$f" .txt).output" "../tests/expected_outputs/$(basename "$f" .txt).expected"`
    if [[ $errors ]]; then
        echo -e "\e[31m$f\e[0m\n$errors"
        exit 1
    else
        echo -e "\e[32m$f\e[0m"
    fi
done
