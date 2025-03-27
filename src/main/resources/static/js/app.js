(function() {
    'use strict';

    angular.module('cartApp', [])
        .controller('CartController', ['$http', function($http) {
            var vm = this;
            vm.cartData = { items: [], totalItems: 0, totalPrice: 0 };

            vm.loadCart = function() {
                // Gọi GET /api/cart (RESTful API)
                $http.get('/api/cart')
                    .then(function(response) {
                        vm.cartData = response.data;
                        console.log("Cart loaded:", vm.cartData);
                    }, function(error) {
                        console.error("Error loading cart:", error);
                        if (error.status === 401) {
                            window.location.href = "/login";
                        }
                    });
            };

            vm.updateQty = function(item) {
                // Gọi POST /api/cart/update/{id}?qty={newQuantity}
                $http.post('/api/cart/update/' + item.id + '?qty=' + item.quantity)
                    .then(function(response) {
                        vm.cartData = response.data;
                        console.log("Updated quantity:", vm.cartData);
                    }, function(error) {
                        console.error("Error updating quantity:", error);
                        if (error.status === 401) {
                            window.location.href = "/login";
                        }
                    });
            };

            vm.removeItem = function(itemId) {
                // Gọi DELETE /api/cart/remove/{id}
                $http.delete('/api/cart/remove/' + itemId)
                    .then(function(response) {
                        vm.cartData = response.data;
                        console.log("Removed item:", vm.cartData);
                    }, function(error) {
                        console.error("Error removing item:", error);
                        if (error.status === 401) {
                            window.location.href = "/login";
                        }
                    });
            };

            // Load giỏ hàng khi controller được khởi tạo
            vm.loadCart();
        }]);
})();
