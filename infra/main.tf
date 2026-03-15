resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"

  tags = {
    Name = "payment-service-vpc"
  }
}

resource "aws_subnet" "main" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "us-east-1a"

  tags = {
    Name = "payment-service-subnet"
  }
}

resource "aws_security_group" "allow_web" {
  name        = "allow_web_traffic"
  description = "Allow HTTP and SSH traffic"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "HTTP from VPC"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "SSH from VPC"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "allow_web"
  }
}

resource "aws_instance" "web" {
  ami           = "ami-0c7217cdde317cfec"
  instance_type = "t2.micro"

  subnet_id                   = aws_subnet.main.id
  vpc_security_group_ids      = [aws_security_group.allow_web.id]
  associate_public_ip_address = true

  user_data = file("${path.module}/setup.sh")

  tags = {
    Name = "payment-service-ec2"
  }
}
