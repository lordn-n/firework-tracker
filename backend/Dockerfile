# Use an official AWS-provided base image for Python Lambda functions
FROM public.ecr.aws/lambda/python:3.9

# Set the working directory in the container to the default Lambda task root
WORKDIR ${LAMBDA_TASK_ROOT}

# Copy the requirements file and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application's code
COPY main.py .

# The command that will be run when the container starts.
# This tells the Lambda service to use the 'handler' object in the 'main' module.
CMD ["main.handler"]
