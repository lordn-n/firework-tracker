# Use an official AWS-provided base image for Python Lambda functions
FROM public.ecr.aws/lambda/python:3.9

# The LAMBDA_TASK_ROOT is /var/task. This is where Lambda looks for the code.
# We will set it as the working directory.
WORKDIR /var/task

# Copy the requirements file and install dependencies.
# The paths are now relative to the WORKDIR.
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application's code
COPY main.py .

# The command that will be run when the container starts.
# This tells the Lambda runtime to find the 'handler' object in the 'main' module.
CMD ["main.handler"]
