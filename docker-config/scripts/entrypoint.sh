#!/bin/bash
#
# Start local dev
#
echo "Starting PostgresML"
service postgresql start

# Setup users
useradd postgresml -m 2> /dev/null 1>&2
sudo -u postgresml touch /home/postgresml/.psql_history
sudo -u postgres createuser root --superuser --login 2> /dev/null 1>&2
sudo -u postgres psql -c "CREATE ROLE postgresml PASSWORD 'postgresml' SUPERUSER LOGIN" 2> /dev/null 1>&2
sudo -u postgres createdb postgresml --owner postgresml 2> /dev/null 1>&2
sudo -u postgres psql -c 'ALTER ROLE postgresml SET search_path TO public,pgml' 2> /dev/null 1>&2
#echo "CREATE SCHEMA IF NOT EXISTS embd"
#sudo -u postgres psql -d postgres -c 'CREATE SCHEMA IF NOT EXISTS embd AUTHORIZATION postgres'

# Create the vector extension
echo "Create the vector extension"
sudo -u postgres psql -c 'CREATE EXTENSION IF NOT EXISTS vector' 2> /dev/null 1>&2
# Create table
#echo "Create table vector_store"
#sudo -u postgres psql -d postgres -U postgres -c 'CREATE TABLE IF NOT EXISTS embd.vector_store (id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,content text,metadata json,embedding vector(768))' 2> /dev/null 1>&2
#echo "CREATE INDEX ON vector_store"
#sudo -u postgres psql -d postgres -U postgres -c 'CREATE INDEX ON embd.vector_store USING HNSW (embedding vector_cosine_ops)'  2> /dev/null 1>&2

#echo "Create the hstore extension"
#sudo -u postgres psql -c 'CREATE EXTENSION IF NOT EXISTS hstore' 2> /dev/null 1>&2
#echo "Create the uuid-ossp extension"
#sudo -u postgres psql -c 'CREATE EXTENSION IF NOT EXISTS "uuid-ossp"' 2> /dev/null 1>&2

# Create the pgml extension
echo "Create the pgml extension"

echo "Starting dashboard"
PGPASSWORD=postgresml psql -c 'CREATE EXTENSION IF NOT EXISTS pgml' \
        -d postgresml \
        -U postgresml \
        -h 127.0.0.1 \
        -p 5433 2> /dev/null 1>&2

bash /app/dashboard.sh &

exec "$@"
# Prevent container from exiting
tail -f /dev/null