param(
    [int]$VUS = 10,
    [string]$DURATION = "10s",
    [string]$FILE = "cdn.pdf"
)

# Διαγραφή και επανεκκίνηση των containers για διαγραφή των παλιών δεδομένων.
docker compose down -v influxdb grafana k6
docker compose up -d influxdb grafana k6

Write-Host "---------------------------------------------"
Write-Host "Load testing with Grafana dashboard: http://localhost:3000"
Write-Host "---------------------------------------------"

docker exec -it k6-container k6 run `
  --out influxdb=http://influxdb:8086/k6 `
  -e VUS=$VUS `
  -e DURATION=$DURATION `
  -e FILE=$FILE `
  /scripts/load_test_script.js