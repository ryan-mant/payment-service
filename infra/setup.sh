#!/bin/bash
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1

echo "--- Starting setup script ---"

apt-get update -y
apt-get install -y git curl

echo "--- Installing Docker ---"
apt-get install -y docker.io
systemctl start docker
systemctl enable docker

echo "--- Installing Docker Compose ---"
LATEST_COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep 'tag_name' | cut -d\" -f4)
curl -L "https://github.com/docker/compose/releases/download/${LATEST_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

usermod -aG docker ubuntu

echo "--- Cloning the payment-service repository ---"
git clone https://github.com/ryan-mant/payment-service.git /home/ubuntu/payment-service

cd /home/ubuntu/payment-service
echo "--- Setting up environment and starting production containers ---"

cp .env.example .env

/usr/local/bin/docker-compose -f docker-compose-prod.yml up -d

echo "--- Setup script finished ---"
