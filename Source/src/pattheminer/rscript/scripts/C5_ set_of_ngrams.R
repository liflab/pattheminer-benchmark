
#-- !!!!! you have to enter arguments window, step , ngrams.size !!!!

#takes as input a file od words and outputs a vector of the frequencies 
#of the words following each other

#------------ N-grams --------
library(stylo)
#library (evobiR)
#library(stringi)



#------------- sliding window modified

SlidingWindow_modified = function(FUN, data, window, step, strict=F, ...){
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
    result <- array(dim= c((window-...+1),(length = length(spots))))
    for(i in 1:length(spots)){
      result[,i] <- match.fun(FUN)(data[spots[i]:(spots[i] + window - 1)],...)
      
    }
  }
  
  # code for matrices
  if(is.matrix(data)){
    total.x <- ncol(data)
    spots.x <- seq(from = 1, to = (total.x - window + 1), by = step)
    total.y <- nrow(data)
    spots.y <- seq(from = 1, to = (total.y - window + 1), by = step)
    result <- matrix(length(spots.y), length(spots.x))
    for(i in 1:length(spots.y)){
      for(j in 1:length(spots.x)){
        result[i, j] <- match.fun(FUN)(data[spots.y[i]:(spots.y[i] + window - 1),
                                            spots.x[j]:(spots.x[j] + window - 1)],...)
      }
    }
  }
  
  # complete failure message
  if(!exists("result")) stop("Hmmm unknown error... Sorry")
  
  # return the result to the user
  return(result)
}





#--------------- args command line ----------

args= commandArgs(trailingOnly = TRUE)
text= unlist((read.csv(args[1], header=TRUE)))


#---- args test

#text=unlist(read.csv(file='ngram_file.csv', header=TRUE))
#----- return word frequecy
words = txt.to.words(text) # turn the string into words

cube_2= SlidingWindow_modified("make.ngrams", words, 
                                window=as.integer(args[2]), 
                                step=as.integer(args[3]), 
                                strict=F, 
                                ngram.size=as.integer(args[4]) )






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

B= c("a a a a", "b b b b", "a b b b", "b a a a")
d=1.25;




# second part that compares the trend with a reference
cube_6=cube_3=array(dim = ncol(cube_2))


for (i in 1:ncol(cube_2)) {
  
  cube_3[i]=(length (setdiff(cube_2[,i], B)))/(length(B))
  
  if(cube_3[i]>d)
  { cube_6[i]= TRUE}
  else{cube_6[i]=FALSE}
}


cube_6
