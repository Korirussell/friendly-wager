#!/bin/bash
set -euo pipefail
# Ensure Java 21 runtime on Elastic Beanstalk Amazon Linux 2 platform
if ! java -version 2>&1 | grep -q 'version "21'; then
  sudo yum install -y java-21-amazon-corretto-headless || true
fi

