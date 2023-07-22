cd backend/ktor-server || exit
./build.sh &
cd ../../frontend || exit
./build.sh &