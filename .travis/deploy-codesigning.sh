#!/usr/bin/env bash

echo "Deploying code signing key..."

openssl aes-256-cbc -K $encrypted_e64895405182_key -iv $encrypted_e64895405182_iv -in ./.travis/codesigning.asc.enc -out ./.travis/codesigning.asc -d
gpg --fast-import .travis/codesigning.asc
