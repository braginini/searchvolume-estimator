#!/bin/bash

set -e
set -x

echo "Starting application now..."

exec java -cp "${SERVICE_LIB_PATH}/*" ${SERVICE_MAIN_CLASS} ${@}
