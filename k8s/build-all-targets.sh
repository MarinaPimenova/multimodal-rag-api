#!/bin/bash

# multimodal-rag-api
./build-target.sh multimodal-rag-api mvn git_pull_no

# rag-spa git_pull_yes
./build-target.sh rag-spa 'npm run build' git_pull_no

