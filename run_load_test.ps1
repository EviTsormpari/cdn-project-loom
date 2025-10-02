param(
    [int]$VUS = 10,
    [string]$DURATION = "60s",
    [string]$FILE = "cdn.pdf"
)

Write-Host "---------------------------------------------"
Write-Host "Load testing with Grafana dashboard: http://localhost:3000"
Write-Host "---------------------------------------------"

docker exec -it k6-container k6 run `
  --out influxdb=http://influxdb:8086/k6 `
  -e VUS=$VUS `
  -e DURATION=$DURATION `
  -e FILE=$FILE `
  /scripts/load_test_script.js