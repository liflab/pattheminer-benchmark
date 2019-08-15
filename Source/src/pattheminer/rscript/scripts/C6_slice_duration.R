
library(rapport)
library(rapportools)


#--------------------modified sliding window  -----------
SlidingWindow2 <- function(FUN, data, window, step, strict=F, orders){
  # Validation testing
  if(strict) {
    
    if(!is.numeric(data)) stop("Please supply numeric data")
    
    if(sum(is.vector(data), is.matrix(data)) == 0) {
      stop("You must supply data as a vector or matrix")
    }
    
    if(is.vector(data)){
      if(window > length(data)) stop("Window size is too large")
      if((step + window) > length(data)) stop("Window and step size 
                                            should be small enough 
                                            that multiple windows 
                                            can be examined")
    }
    
    if(is.matrix(data)){
      if(window > nrow(data)) stop("Window size is too large for number of rows")
      if(window > ncol(data)) stop("Window size is too large for number of cols")
      if((step + window) > nrow(data)) stop("Window and step size 
                                            should be small enough 
                                            that multiple windows 
                                            can be examined")
    }
  }
  
  # code for vectors
  if(is.vector(data)) {
    total <- length(data)
    spots <- seq(from = 1, to = (total - window + 1), by = step)
    result <- vector(length = length(spots))
    for(i in 1:length(spots)){
      result[i] <- match.fun(FUN)(data[spots[i]:(spots[i] + window - 1)], orders)
      
    }
  }
  
  # code for matrices
  if(is.matrix(data)){
    total.y <- nrow(data)
    spots.y <- seq(from = 1, to = (total.y - window + 1), by = step)
    result <- array(dim=length(spots.y))
    for(i in 1:length(spots.y)){
      result[i] <- match.fun(FUN)(data[spots.y[i]:(spots.y[i] + window - 1),1:ncol(data)])
      
    }
  }
  
  # complete failure message
  if(!exists("result")) stop("Hmmm unknown error... Sorry")
  
  # return the result to the user
  return(result)
}

#############################################################


#--------- passsing arguments ---------

args= commandArgs(trailingOnly = TRUE) # receive args
data_csv= read.csv(args[1], header = FALSE, sep=",")
data=as.matrix(data_csv)

window= as.integer(args[2])
step= as.integer(args[3])


#---------------- slice duration function ----------
Slice_duration= function (data){
  
  
  save=array(dim = c(nrow(data),4),0) #array where I save all the ID, timestamps and durations
  
  sum=0
  taille=0
  for(i in 1:nrow(data))
  {
    
    if(data[i,3]=="Start")
    {
      save[i,2]=data[i,2] #saves timestamp start
      save[i,1]= data[i,1] # saves id
    }
    
    if(data[i,3]=="zwaye")
    {
      
      if((is.empty(save[1,1]))==FALSE){
        
        for(j in 1:i)
        {
          
          if(save[j,1]==data[i,1])
          {
            save[j,3]=data[i,2] # timestamp end 
            save[j,4]=as.integer(save[j,3])-as.integer(save[j,2]) #duration
            
            sum=sum+as.integer(save[j,4]) #sum of durations
            
            taille=taille+1 #number of the traces that end incide the window
            
            
            break()
          }
          
        }
      }
      
      
      
    }
    
  }
  
  if(sum==0)
  {moyenne=0}else{ 
    
    moyenne=sum/taille  #average duration of traces
  }
  return(moyenne)
  
}

cube_2=SlidingWindow2( "Slice_duration", data, window, step)
#--------- second part ---------

###########
###     ###
###      ##
###     ###
###########

   ### 
   ###
 #######
  #####
   ###
    #



P=1000
d=10



cube_6=cube_3=array(dim = length(cube_2))


for (i in 1:length(cube_2)) {
  cube_3[i]= abs(cube_2[i]-P);
  if(cube_3[i]>d)
  { cube_6[i]= TRUE}
  else{cube_6[i]=FALSE}
}

#cube_2
#cube_3
cube_6



