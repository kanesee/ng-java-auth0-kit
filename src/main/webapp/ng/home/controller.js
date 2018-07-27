app.controller('homeCtrl',
  ['$scope'
  ,'$http'
  ,homeCtrl
  ]);

function homeCtrl($scope
                , $http
                      ) {
  $scope.error = null;
  $scope.message = null;
    
  // private vars
  var self = this;
  
  // private functions
  this.init = function() {
    self.getUser();
  }
  
  this.getUser = function() {
    $http.get('/auth0-kit/rest/myresource/user')
      .then(function(res) {
        $scope.user = res.data;
      })
      .catch(function(err) {
        $scope.error = err;
      })
  }
 
  this.init();
}