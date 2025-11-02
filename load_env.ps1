Write-Host "Loading environment variables from .env..."
Get-Content .env | ForEach-Object {
    if ($_ -match "^\s*([^#]\S+?)=(.+)$") {
        $name = $matches[1]
        $value = $matches[2]
        [System.Environment]::SetEnvironmentVariable($name, $value, 'Process')
    }
}
Write-Host "Loaded succesfully"