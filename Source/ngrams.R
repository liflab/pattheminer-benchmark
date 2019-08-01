
#-- !!!!! you have to enter arguments window, step , ngrams.size !!!!

#takes as input a file od words and outputs a vector of the frequencies of the words following each other

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



#------ generation de character------------
#my.text= c("massiva","mohamed","amazigh","gaya","maman","roza","dihia", "celia", "rabha", "ines", "bouchera", "aya","lwiz", "zazi", "ghiles", "massi")
#write.csv(my.text, file='ngram_file.csv', row.names=FALSE)

#--------------- args command line ----------

args= commandArgs(trailingOnly = TRUE)
text= ((read.csv(args [1], header=TRUE)))

text=text[1] # to select the column that contains the labels 
text=unlist(text) # we have to unlist the vector If we don't there will be a conflict of types
#---- args test

#text=unlist(read.csv(file='ngram_file.csv', header=TRUE))
#----- return word frequecy
words = txt.to.words(text) # turn the string into words
n.grams= SlidingWindow_modified("make.ngrams", words, window=as.integer(args[2]), step=as.integer(args[3]), strict=F, ngram.size=as.integer(args[4]))
n.grams.frequency= make.frequency.list(as.character (n.grams), value = TRUE)

print('The n-grams frequencies are: ')
n.grams.frequency


