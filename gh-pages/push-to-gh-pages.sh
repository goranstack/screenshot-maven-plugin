#!/bin/bash
cd asciidoc/target
rm -rf site
git init
git remote add origin git@github.com:goranstack/screenshot-maven-plugin.git
git add .
git commit -m "Updated generated doc"
git push --force origin master:gh-pages
rm -rf .git 