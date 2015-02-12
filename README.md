# plantuml-view

simple plantuml diagram viewer

this command checks a conditionof 'target-file', if the file was changed, it will update diagrams.

I intend to use it with emacs or some other editors. (misc/my-plantuml.el)


## how to use
```
plantuml-view 'target-file'
```

## build
```
./gradlew distZip
```
or
```
./gradlew installApp
```
default destination is ~/bin/plantuml-view

