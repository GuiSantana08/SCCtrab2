# Maven
* To compile:
    - mvn clean compile package

# Docker
* create image:
    - docker build -t <gmcampos/tagname> <directory>

* push image:
    - docker push <gmcampos/tagname>

* run docker locally:
    - docker run --rm -p 8080:8080 <gmcampos/tagname>

# Azure Docker
* create cluster:
    - az group create --name scc2324-cluster-60182 --location northeurope

* start container:
    - az container create --resource-group scc2324-cluster-60182-westus --name scc2324-tests-westus --image guisantana08/scc2324-tests --ports 8080 --dns-name-label scc-tests-60182-westus

* delete container: 
    - az container delete --resource-group scc2324-cluster-60182 --name scc2324-tests

* logs:
    - az container logs --resource-group scc2324-cluster-60182-westus --name scc2324-tests-westus

# Kubernetes
* create a service:
    -  az ad sp create-for-rbac --name http://scc2324-kuber --role Contributor --scope /subscriptions/<ID_OF_YOUR_SUBSCRIPTION>

* create a cluster:
    - az aks create --resource-group scc2324-cluster-60353 --name scc2324-cluster-60353 --node-vm-size Standard_B2s --generate-ssh-keys 
      --node-count 2 --service-principal <appId_REPLACE> --client-secret <password_REPLACE>

* get credentials:
    - az aks get-credentials --resource-group scc2324-cluster-60353 --name my-scc2324-cluster-60353

* deploy file:
    - kubectl apply -f azure-vote.yaml

* check service:
    - kubectl get services

* check pods:
    - kubectl get pods

* Stream Logs:
    - kubectl logs -f <Name>

* Delete All Objects:
    -  kubectl delete deployments,services,pods --all

* Delete All Persistent Volumes:
    - kubectl delete pv --all

* Delete Cluster
    - az group delete --resource-group scc2324-cluster-60353